package jpaoletti.jpm2.core.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jpaoletti.jpm2.util.JPMUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Applies the DDL of a classpath {@code database.sql} at boot, keeping the historical
 * {@code -- @@ N} marker format and semantics of the legacy {@code DBSyncService} (one statement
 * per marker, executed in file order, continue-on-error), but:
 *
 * <ul>
 *   <li>tracks the applied revision in its own {@code jpm_ddl_migration} history table
 *       (one row per revision, with outcome/error/duration) instead of a {@code ConfigService}
 *       counter — so it no longer depends on the legacy config store;</li>
 *   <li>records failures instead of losing them as a silent WARNING;</li>
 *   <li>is cluster-safe via a MySQL advisory lock so two instances booting at once do not
 *       apply migrations concurrently;</li>
 *   <li>reconciles histories written by the legacy sequential-number bug by exact SQL match,
 *       warning and continuing when a historical row cannot be mapped unambiguously.</li>
 * </ul>
 *
 * <p>Everything runs on a single JDBC connection (via {@link Session#doWork}) so the advisory
 * lock stays held for the whole run. The history table is created by the service itself
 * ({@code CREATE TABLE IF NOT EXISTS}) so it works even where {@code hbm2ddl} is disabled.
 * Each non-empty revision ends with an explicit JDBC commit, independently of the connection's
 * initial auto-commit setting. Failed DML is rolled back before its failure is recorded.
 * If a block contains multiple top-level semicolon-delimited statements, only the first is
 * executed and the ignored remainder is reported as a warning.
 *
 * <p>First run after cutover: if the history table is empty, the starting revision is bridged
 * once from the legacy {@code configs.database-revision} row (read with raw SQL, tolerant to its
 * absence), so databases already migrated by the old runner are not replayed.
 *
 * @author jpaoletti
 */
public class DdlMigrationService {

    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

    /** Marker prefix; a revision line looks like {@code -- @@ 1234}. */
    private static final String SYNC_DB_START_TOKEN = "-- @@";
    private static final String RESOURCE = "database.sql";
    private static final String LOCK_NAME = "jpm_ddl_migration";
    private static final int LOCK_TIMEOUT_SECONDS = 60;
    private static final String BASELINE_STATEMENT = "(baseline from legacy database-revision)";
    private static final int MAX_DIAGNOSTIC_SAMPLES = 10;

    /**
     * Best-effort boot hook: migration problems are reported but never prevent the application
     * context from starting. Ambiguous history rows are ignored so later revisions can run.
     */
    public void sync() {
        try {
            doSync();
        } catch (IllegalStateException e) {
            JPMUtils.getLogger().warn("DDL migration: se omite la sincronizacion y el arranque continua: "
                    + e.getMessage());
        } catch (Exception e) {
            JPMUtils.getLogger().warn(
                    "DDL migration: fallo inesperado; se omite la sincronizacion y el arranque continua", e);
        }
    }

    void doSync() throws Exception {
        final InputStream is = getClass().getClassLoader().getResourceAsStream(RESOURCE);
        if (is == null) {
            JPMUtils.getLogger().warn("DDL migration: no se encontro '" + RESOURCE + "' en el classpath; nada que aplicar");
            return;
        }
        is.close();
        try (Session session = sessionFactory.openSession()) {
            session.doWork(this::run);
        }
    }

    /** Runs the whole migration on a single connection so the advisory lock stays held. */
    void run(Connection conn) throws SQLException {
        final boolean originalAutoCommit = conn.getAutoCommit();
        boolean locked = false;
        try {
            // Hibernate/DataSource configurations differ on the initial auto-commit value. Use
            // explicit commit points in both cases so DML is not rolled back on Session close,
            // and so the history row following a MySQL implicit DDL commit is not lost.
            if (originalAutoCommit) {
                conn.setAutoCommit(false);
            }

            final Integer lock = tryGetLock(conn);
            if (lock != null && lock == 0) {
                JPMUtils.getLogger().warn("DDL migration: otra instancia tiene el lock '" + LOCK_NAME + "'; se omite en esta instancia");
                return;
            }
            locked = lock != null && lock == 1;

            ensureTable(conn);
            conn.commit();
            final int current = getCurrentRevision(conn);
            conn.commit();
            applyPending(conn, current);
        } finally {
            rollbackQuietly(conn);
            if (locked) {
                releaseLock(conn);
                rollbackQuietly(conn);
            }
            if (originalAutoCommit) {
                conn.setAutoCommit(true);
            }
        }
    }

    /** Leaves no unfinished transaction on the Hibernate-managed connection. */
    private void rollbackQuietly(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException e) {
            JPMUtils.getLogger().warn("DDL migration: no se pudo limpiar la transaccion JDBC", e);
        }
    }

    // --- advisory lock (MySQL; degrades to no-lock on other engines) --------

    private Integer tryGetLock(Connection conn) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT GET_LOCK(?, ?)")) {
            ps.setString(1, LOCK_NAME);
            ps.setInt(2, LOCK_TIMEOUT_SECONDS);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    final int v = rs.getInt(1);
                    return rs.wasNull() ? null : v;
                }
            }
        } catch (SQLException e) {
            JPMUtils.getLogger().warn("DDL migration: GET_LOCK no soportado; se continua sin lock", e);
        }
        return null;
    }

    private void releaseLock(Connection conn) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT RELEASE_LOCK(?)")) {
            ps.setString(1, LOCK_NAME);
            ps.executeQuery();
        } catch (SQLException e) {
            JPMUtils.getLogger().warn("DDL migration: no se pudo liberar el lock '" + LOCK_NAME + "'", e);
        }
    }

    // --- table + current revision -------------------------------------------

    private void ensureTable(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS jpm_ddl_migration ("
                    + "id BIGINT NOT NULL AUTO_INCREMENT,"
                    + "revision INT NOT NULL,"
                    + "statement LONGTEXT,"
                    + "success CHAR(1) DEFAULT 'Y',"
                    + "error LONGTEXT,"
                    + "applied_at DATETIME,"
                    + "duration_ms BIGINT,"
                    + "PRIMARY KEY (id),"
                    + "UNIQUE KEY jpm_ddl_migration_revision_uq (revision)"
                    + ") ENGINE=InnoDB");
        }
    }

    /**
     * Current revision = {@code MAX(revision)} of the history table. On an empty table, bridge
     * once from the legacy {@code configs.database-revision} value (0 if absent) and record it as
     * a baseline row so already-migrated databases are not replayed.
     */
    private int getCurrentRevision(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT MAX(revision) FROM jpm_ddl_migration")) {
            if (rs.next()) {
                final int max = rs.getInt(1);
                if (!rs.wasNull()) {
                    return max;
                }
            }
        }
        final int baseline = readLegacyRevision(conn);
        record(conn, baseline, BASELINE_STATEMENT, true, null, 0L);
        JPMUtils.getLogger().info(String.format("DDL migration: historial vacio, baseline en revision %d (legacy database-revision)", baseline));
        return baseline;
    }

    /** Reads the legacy {@code configs.database-revision}; returns 0 if the table/row is absent. */
    private int readLegacyRevision(Connection conn) {
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT value FROM configs WHERE code = 'database-revision'")) {
            if (rs.next()) {
                final String v = rs.getString(1);
                if (v != null && !v.trim().isEmpty()) {
                    return Integer.parseInt(v.trim());
                }
            }
        } catch (SQLException | NumberFormatException e) {
            JPMUtils.getLogger().info("DDL migration: sin revision legacy en configs (o tabla ausente); baseline en 0");
        }
        return 0;
    }

    // --- apply -------------------------------------------------------------

    private void applyPending(Connection conn, int current) throws SQLException {
        final List<MigrationBlock> migrations;
        try {
            migrations = readMigrations();
        } catch (java.io.IOException e) {
            JPMUtils.getLogger().warn("DDL migration: error leyendo " + RESOURCE + "; no se aplicaran migraciones", e);
            return;
        }
        applyMigrations(conn, current, migrations);
    }

    /** Applies every block newer than the best revision that can be inferred from history. */
    void applyMigrations(Connection conn, int current, List<MigrationBlock> migrations) {
        int effectiveCurrent = current;
        try {
            effectiveCurrent = reconcileLegacyRevisionNumbers(conn, current, migrations);
        } catch (Exception e) {
            JPMUtils.getLogger().warn(String.format(
                    "DDL migration: no se pudo reconciliar el historial; se continuara desde "
                    + "la revision %d", current), e);
            rollbackQuietly(conn);
        }
        for (MigrationBlock migration : migrations) {
            if (migration.revision() > effectiveCurrent) {
                try {
                    executeOne(conn, migration.revision(), migration.statement());
                } catch (Exception e) {
                    JPMUtils.getLogger().warn(String.format(
                            "DDL migration: revision %d no pudo completarse; se continua con "
                            + "la siguiente", migration.revision()), e);
                    rollbackQuietly(conn);
                }
            }
        }
    }

    /** Reads and validates the whole script once, preserving each marker's declared revision. */
    private List<MigrationBlock> readMigrations() throws java.io.IOException {
        final InputStream is = getClass().getClassLoader().getResourceAsStream(RESOURCE);
        if (is == null) {
            throw new java.io.IOException("No se encontro '" + RESOURCE + "' en el classpath");
        }
        try (InputStream input = is;
                BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            return readMigrations(reader);
        }
    }

    List<MigrationBlock> readMigrations(BufferedReader reader) throws java.io.IOException {
        final List<MigrationBlock> migrations = new ArrayList<>();
        Integer previous = null;
        Integer revision = null;
        StringBuilder sql = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith(SYNC_DB_START_TOKEN)) {
                final Integer nextRevision = revisionOfOrNull(line);
                if (nextRevision == null) {
                    if (revision != null) {
                        migrations.add(new MigrationBlock(revision, sql.toString().trim()));
                    }
                    revision = null;
                    sql = new StringBuilder();
                    continue;
                }
                if (previous != null && nextRevision <= previous) {
                    JPMUtils.getLogger().warn(String.format(
                            "DDL migration: revisiones fuera de orden: %d seguida de %d; se continua",
                            previous, nextRevision));
                }
                if (revision != null) {
                    migrations.add(new MigrationBlock(revision, sql.toString().trim()));
                }
                previous = nextRevision;
                revision = nextRevision;
                sql = new StringBuilder();
            } else if (revision != null && !line.startsWith("--")) {
                sql.append(line).append('\n');
            }
        }
        if (revision != null) {
            migrations.add(new MigrationBlock(revision, sql.toString().trim()));
        }
        return migrations;
    }

    /**
     * Recovers the effective revision for histories written by the old {@code rev++} algorithm.
     * Matching is exact and O(script blocks + history rows). Ambiguous or unknown rows are warned
     * and ignored so later revisions can still run.
     */
    int reconcileLegacyRevisionNumbers(Connection conn, int current, List<MigrationBlock> migrations) throws SQLException {
        final long startedAt = System.currentTimeMillis();
        final Map<String, Integer> uniqueRevisionByStatement = new HashMap<>();
        final Set<String> ambiguousStatements = new HashSet<>();
        final Map<Integer, String> statementByRevision = new HashMap<>();
        for (MigrationBlock migration : migrations) {
            if (migration.statement().isEmpty()) {
                continue;
            }
            statementByRevision.put(migration.revision(), migration.statement());
            if (!ambiguousStatements.contains(migration.statement())) {
                final Integer previous = uniqueRevisionByStatement.putIfAbsent(
                        migration.statement(), migration.revision());
                if (previous != null) {
                    uniqueRevisionByStatement.remove(migration.statement());
                    ambiguousStatements.add(migration.statement());
                }
            }
        }

        int effectiveCurrent = current;
        int historyRows = 0;
        int recovered = 0;
        int uncertain = 0;
        final List<String> recoveredSamples = new ArrayList<>();
        final List<String> uncertainSamples = new ArrayList<>();
        try (Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(
                        "SELECT revision, statement FROM jpm_ddl_migration ORDER BY id")) {
            while (rs.next()) {
                historyRows++;
                final int recordedRevision = rs.getInt(1);
                final String statement = rs.getString(2);
                if (statement == null || BASELINE_STATEMENT.equals(statement)) {
                    continue;
                }

                final Integer declaredRevision = uniqueRevisionByStatement.get(statement);
                if (declaredRevision != null) {
                    if (declaredRevision != recordedRevision) {
                        effectiveCurrent = Math.max(effectiveCurrent, declaredRevision);
                        recovered++;
                        addDiagnostic(recoveredSamples, String.format(
                                "registrada=%d, declarada=%d", recordedRevision, declaredRevision));
                    }
                    continue;
                }

                if (ambiguousStatements.contains(statement)
                        && statement.equals(statementByRevision.get(recordedRevision))) {
                    continue;
                }
                uncertain++;
                addDiagnostic(uncertainSamples, String.format(
                        "revision=%d, sql=%s", recordedRevision, sqlPreview(statement)));
            }
        }

        final long durationMs = System.currentTimeMillis() - startedAt;
        if (uncertain > 0) {
            JPMUtils.getLogger().warn(String.format(
                    "DDL migration: historial incompatible o ambiguo (%d de %d filas, %d ms); "
                    + "se continuara desde la revision efectiva %d. Casos: %s",
                    uncertain, historyRows, durationMs, effectiveCurrent,
                    String.join(" | ", uncertainSamples)));
        }
        if (recovered > 0) {
            JPMUtils.getLogger().warn(String.format(
                    "DDL migration: se reconciliaron %d revisiones historicas con numeracion legacy (%s)",
                    recovered, String.join(" | ", recoveredSamples)));
        }
        JPMUtils.getLogger().info(String.format(
                "DDL migration: preflight %d bloques, %d filas historicas, %d reconciliadas, "
                + "revision efectiva %d, %d ms",
                migrations.size(), historyRows, recovered, effectiveCurrent, durationMs));
        return effectiveCurrent;
    }

    private void addDiagnostic(List<String> diagnostics, String diagnostic) {
        if (diagnostics.size() < MAX_DIAGNOSTIC_SAMPLES) {
            diagnostics.add(diagnostic);
        }
    }

    private String sqlPreview(String statement) {
        final String singleLine = statement.replaceAll("\\s+", " ").trim();
        return singleLine.length() <= 120 ? singleLine : singleLine.substring(0, 117) + "...";
    }

    /**
     * Executes one parsed block and records its outcome. Mirrors the legacy runner: one statement
     * per marker, continue-on-error.
     */
    void executeOne(Connection conn, int revision, String stmt) throws SQLException {
        if (!stmt.isEmpty()) {
            final SqlSelection selection = selectFirstStatement(stmt);
            if (selection.hasIgnoredSql()) {
                JPMUtils.getLogger().warn(String.format(
                        "DDL migration: revision %d contiene multiples sentencias; se ejecutara "
                        + "solo la primera y se ignorara el resto: %s",
                        revision, sqlPreview(selection.ignoredSql())));
            }
            final long t0 = System.currentTimeMillis();
            boolean ok = true;
            String error = null;
            try (Statement st = conn.createStatement()) {
                JPMUtils.getLogger().info(String.format("DDL migration: aplicando revision %d", revision));
                st.execute(selection.firstStatement());
            } catch (SQLException e) {
                ok = false;
                error = e.getMessage();
                JPMUtils.getLogger().warn(String.format("DDL migration: revision %d FALLIDA: %s", revision, stmt), e);
                conn.rollback();
            }
            record(conn, revision, stmt, ok, error, System.currentTimeMillis() - t0);
            conn.commit();
        }
    }

    /** Selects only the first top-level SQL statement; semicolons inside literals/comments are ignored. */
    SqlSelection selectFirstStatement(String sql) {
        final int end = firstStatementEnd(sql);
        if (end < 0) {
            return new SqlSelection(sql.trim(), "");
        }
        final String first = sql.substring(0, end).trim();
        final String remainder = sql.substring(end).trim();
        return new SqlSelection(first, containsExecutableSql(remainder) ? remainder : "");
    }

    private int firstStatementEnd(String sql) {
        char quote = 0;
        boolean lineComment = false;
        boolean blockComment = false;
        for (int i = 0; i < sql.length(); i++) {
            final char c = sql.charAt(i);
            if (lineComment) {
                if (c == '\n' || c == '\r') {
                    lineComment = false;
                }
                continue;
            }
            if (blockComment) {
                if (c == '*' && i + 1 < sql.length() && sql.charAt(i + 1) == '/') {
                    blockComment = false;
                    i++;
                }
                continue;
            }
            if (quote != 0) {
                if (c == '\\' && i + 1 < sql.length()) {
                    i++;
                } else if (c == quote) {
                    if (i + 1 < sql.length() && sql.charAt(i + 1) == quote) {
                        i++;
                    } else {
                        quote = 0;
                    }
                }
                continue;
            }

            if (c == '\'' || c == '"' || c == '`') {
                quote = c;
            } else if (isDashCommentStart(sql, i) || c == '#') {
                lineComment = true;
                if (c == '-') {
                    i++;
                }
            } else if (c == '/' && i + 1 < sql.length() && sql.charAt(i + 1) == '*') {
                blockComment = true;
                i++;
            } else if (c == ';') {
                return i + 1;
            }
        }
        return -1;
    }

    private boolean containsExecutableSql(String sql) {
        boolean lineComment = false;
        boolean blockComment = false;
        for (int i = 0; i < sql.length(); i++) {
            final char c = sql.charAt(i);
            if (lineComment) {
                if (c == '\n' || c == '\r') {
                    lineComment = false;
                }
                continue;
            }
            if (blockComment) {
                if (c == '*' && i + 1 < sql.length() && sql.charAt(i + 1) == '/') {
                    blockComment = false;
                    i++;
                }
                continue;
            }
            if (Character.isWhitespace(c) || c == ';') {
                continue;
            }
            if (isDashCommentStart(sql, i) || c == '#') {
                lineComment = true;
                if (c == '-') {
                    i++;
                }
                continue;
            }
            if (c == '/' && i + 1 < sql.length() && sql.charAt(i + 1) == '*') {
                if (i + 2 < sql.length() && sql.charAt(i + 2) == '!') {
                    return true;
                }
                blockComment = true;
                i++;
                continue;
            }
            return true;
        }
        return false;
    }

    private boolean isDashCommentStart(String sql, int index) {
        return sql.charAt(index) == '-'
                && index + 1 < sql.length()
                && sql.charAt(index + 1) == '-'
                && (index + 2 >= sql.length() || Character.isWhitespace(sql.charAt(index + 2)));
    }

    /** Inserts one history row. Best-effort: a failure here must not abort the migration run. */
    private void record(Connection conn, int revision, String statement, boolean success, String error, long durationMs) {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO jpm_ddl_migration (revision, statement, success, error, applied_at, duration_ms) VALUES (?, ?, ?, ?, ?, ?)")) {
            ps.setInt(1, revision);
            ps.setString(2, statement);
            ps.setString(3, success ? "Y" : "N");
            ps.setString(4, error);
            ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            ps.setLong(6, durationMs);
            ps.executeUpdate();
        } catch (SQLException e) {
            JPMUtils.getLogger().warn(String.format("DDL migration: no se pudo registrar la revision %d en el historial", revision), e);
        }
    }

    /** Parses the revision number from a marker line such as {@code -- @@ 1234}. */
    private int revisionOf(String markerLine) {
        return Integer.parseInt(markerLine.substring(SYNC_DB_START_TOKEN.length()).trim());
    }

    private Integer revisionOfOrNull(String markerLine) {
        try {
            return revisionOf(markerLine);
        } catch (NumberFormatException e) {
            JPMUtils.getLogger().warn("DDL migration: marcador invalido; se omite su bloque: "
                    + markerLine);
            return null;
        }
    }

    record MigrationBlock(int revision, String statement) {
    }

    record SqlSelection(String firstStatement, String ignoredSql) {

        boolean hasIgnoredSql() {
            return !ignoredSql.isEmpty();
        }
    }
}
