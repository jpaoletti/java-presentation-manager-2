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
 *       apply migrations concurrently.</li>
 * </ul>
 *
 * <p>Everything runs on a single JDBC connection (via {@link Session#doWork}) so the advisory
 * lock stays held for the whole run. The history table is created by the service itself
 * ({@code CREATE TABLE IF NOT EXISTS}) so it works even where {@code hbm2ddl} is disabled.
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
    private static final String SYNC_DB_START_TOKEN = "-- @";
    private static final String RESOURCE = "database.sql";
    private static final String LOCK_NAME = "jpm_ddl_migration";
    private static final int LOCK_TIMEOUT_SECONDS = 60;

    public void sync() throws Exception {
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
    private void run(Connection conn) throws SQLException {
        final Integer lock = tryGetLock(conn);
        if (lock != null && lock == 0) {
            JPMUtils.getLogger().warn("DDL migration: otra instancia tiene el lock '" + LOCK_NAME + "'; se omite en esta instancia");
            return;
        }
        final boolean locked = lock != null && lock == 1;
        try {
            ensureTable(conn);
            final int current = getCurrentRevision(conn);
            applyPending(conn, current);
        } finally {
            if (locked) {
                releaseLock(conn);
            }
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
        record(conn, baseline, "(baseline from legacy database-revision)", true, null, 0L);
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
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(RESOURCE);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            final Integer next = discardExecuted(reader, current);
            if (next == null) {
                return;
            }
            int rev = next;
            while (executeOne(conn, reader, rev)) {
                rev++;
            }
        } catch (java.io.IOException e) {
            JPMUtils.getLogger().error("DDL migration: error leyendo " + RESOURCE, e);
        }
    }

    /** Positions the reader on the first marker whose revision is greater than {@code current}. */
    private Integer discardExecuted(BufferedReader reader, int current) throws java.io.IOException {
        String s = reader.readLine();
        while (s != null) {
            s = s.trim();
            if (s.startsWith(SYNC_DB_START_TOKEN)) {
                final int r = revisionOf(s);
                if (r > current) {
                    return r;
                }
            }
            s = reader.readLine();
        }
        return null;
    }

    /**
     * Executes the block (lines up to the next marker) for {@code revision}, records the outcome,
     * and returns whether another block follows. Mirrors the legacy runner: one statement per
     * marker, continue-on-error.
     */
    private boolean executeOne(Connection conn, BufferedReader reader, int revision) throws java.io.IOException {
        String s = reader.readLine();
        final StringBuilder sql = new StringBuilder();
        while (s != null && !s.trim().startsWith(SYNC_DB_START_TOKEN)) {
            final String line = s.trim();
            if (!line.startsWith("--")) {
                sql.append(line).append('\n');
            }
            s = reader.readLine();
        }
        final String stmt = sql.toString().trim();
        if (!stmt.isEmpty()) {
            final long t0 = System.currentTimeMillis();
            boolean ok = true;
            String error = null;
            try (Statement st = conn.createStatement()) {
                JPMUtils.getLogger().info(String.format("DDL migration: aplicando revision %d", revision));
                st.execute(stmt);
            } catch (SQLException e) {
                ok = false;
                error = e.getMessage();
                JPMUtils.getLogger().warn(String.format("DDL migration: revision %d FALLIDA: %s", revision, stmt), e);
            }
            record(conn, revision, stmt, ok, error, System.currentTimeMillis() - t0);
        }
        return s != null;
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
            JPMUtils.getLogger().error(String.format("DDL migration: no se pudo registrar la revision %d en el historial", revision), e);
        }
    }

    /** Parses the revision number from a marker line such as {@code -- @@ 1234}. */
    private int revisionOf(String markerLine) {
        return Integer.parseInt(markerLine.substring(SYNC_DB_START_TOKEN.length() + 1).trim());
    }
}
