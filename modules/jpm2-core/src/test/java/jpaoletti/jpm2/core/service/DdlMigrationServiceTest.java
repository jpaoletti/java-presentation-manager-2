package jpaoletti.jpm2.core.service;

import java.io.BufferedReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Table;
import jpaoletti.jpm2.core.model.persistent.DdlMigration;
import org.hibernate.annotations.Formula;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DdlMigrationServiceTest {

    @Test
    public void ambiguousHistoryNeverBlocksApplicationStartup() throws Exception {
        final DdlMigrationService service = spy(new DdlMigrationService());
        doThrow(new IllegalStateException("ambiguous history")).when(service).doSync();

        assertDoesNotThrow(service::sync);
    }

    @Test
    public void unexpectedMigrationFailureNeverBlocksApplicationStartup() throws Exception {
        final DdlMigrationService service = spy(new DdlMigrationService());
        doThrow(new SQLException("database unavailable")).when(service).doSync();

        assertDoesNotThrow(service::sync);
    }

    @Test
    public void runTemporarilyDisablesAndRestoresAutoCommit() throws Exception {
        final Connection conn = lockTimeoutConnection(true);

        new DdlMigrationService().run(conn);

        final InOrder order = inOrder(conn);
        order.verify(conn).setAutoCommit(false);
        order.verify(conn).rollback();
        order.verify(conn).setAutoCommit(true);
    }

    @Test
    public void runPreservesDisabledAutoCommit() throws Exception {
        final Connection conn = lockTimeoutConnection(false);

        new DdlMigrationService().run(conn);

        verify(conn, never()).setAutoCommit(anyBoolean());
        verify(conn).rollback();
    }

    @Test
    public void successfulStatementAndHistoryAreCommittedTogether() throws Exception {
        final Connection conn = mock(Connection.class);
        final Statement statement = mock(Statement.class);
        final PreparedStatement history = mock(PreparedStatement.class);
        when(conn.createStatement()).thenReturn(statement);
        when(conn.prepareStatement(contains("INSERT INTO jpm_ddl_migration"))).thenReturn(history);

        new DdlMigrationService().executeOne(
                conn, "", 1, "INSERT INTO example(value) VALUES (1)");

        final InOrder order = inOrder(statement, history, conn);
        order.verify(statement).execute("INSERT INTO example(value) VALUES (1)");
        order.verify(history).executeUpdate();
        order.verify(conn).commit();
        verify(conn, never()).rollback();
    }

    @Test
    public void failedDmlIsRolledBackBeforeFailureHistoryIsCommitted() throws Exception {
        final Connection conn = mock(Connection.class);
        final Statement statement = mock(Statement.class);
        final PreparedStatement history = mock(PreparedStatement.class);
        when(conn.createStatement()).thenReturn(statement);
        when(conn.prepareStatement(contains("INSERT INTO jpm_ddl_migration"))).thenReturn(history);
        when(statement.execute("UPDATE example SET value = 2"))
                .thenThrow(new SQLException("expected failure"));

        new DdlMigrationService().executeOne(
                conn, "", 2, "UPDATE example SET value = 2");

        final InOrder order = inOrder(statement, history, conn);
        order.verify(statement).execute("UPDATE example SET value = 2");
        order.verify(conn).rollback();
        order.verify(history).executeUpdate();
        order.verify(conn).commit();
    }

    @Test
    public void multipleStatementsExecuteOnlyTheFirstAndRecordTheCompleteBlock() throws Exception {
        final Connection conn = mock(Connection.class);
        final Statement statement = mock(Statement.class);
        final PreparedStatement history = mock(PreparedStatement.class);
        when(conn.createStatement()).thenReturn(statement);
        when(conn.prepareStatement(contains("INSERT INTO jpm_ddl_migration"))).thenReturn(history);
        final String block = "INSERT INTO example(value) VALUES (1);\n"
                + "UPDATE example SET value = 2;";

        new DdlMigrationService().executeOne(conn, "", 3, block);

        verify(statement).execute("INSERT INTO example(value) VALUES (1);");
        verify(statement, never()).execute(contains("UPDATE"));
        verify(history).setString(1, "");
        verify(history).setString(3, block);
        verify(history).executeUpdate();
        verify(conn).commit();
    }

    @Test
    public void statementSelectionIgnoresSemicolonsInsideQuotesAndComments() {
        final DdlMigrationService service = new DdlMigrationService();
        final String sql = "INSERT INTO example(text, identifier) VALUES ('a;''b', `column;name`);"
                + " -- trailing ; comment\n/* block ; comment */";

        final DdlMigrationService.SqlSelection selection = service.selectFirstStatement(sql);

        assertEquals("INSERT INTO example(text, identifier) VALUES ('a;''b', `column;name`);",
                selection.firstStatement());
        assertFalse(selection.hasIgnoredSql());
    }

    @Test
    public void statementSelectionFindsSqlAfterCommentsAndEmptyTerminators() {
        final DdlMigrationService.SqlSelection selection = new DdlMigrationService().selectFirstStatement(
                "SELECT 1; -- separator ;\n/* comment ; */ ;; UPDATE example SET value = 2;");

        assertEquals("SELECT 1;", selection.firstStatement());
        assertTrue(selection.hasIgnoredSql());
        assertEquals("-- separator ;\n/* comment ; */ ;; UPDATE example SET value = 2;",
                selection.ignoredSql());
    }

    @Test
    public void executeOneUsesTheActualNumberOfEveryMarker() throws Exception {
        final Connection conn = mock(Connection.class);
        final Statement firstStatement = mock(Statement.class);
        final Statement secondStatement = mock(Statement.class);
        final PreparedStatement history = mock(PreparedStatement.class);
        when(conn.createStatement()).thenReturn(firstStatement, secondStatement);
        when(conn.prepareStatement(contains("INSERT INTO jpm_ddl_migration"))).thenReturn(history);
        final List<DdlMigrationService.MigrationBlock> migrations = new DdlMigrationService().readMigrations(reader(
                "-- @@ 10\n"
                + "INSERT INTO example(value) VALUES (10)\n"
                + "-- @@ 20\n"
                + "INSERT INTO example(value) VALUES (20)"));

        final DdlMigrationService service = new DdlMigrationService();
        for (DdlMigrationService.MigrationBlock migration : migrations) {
            service.executeOne(conn, migration.tag(), migration.revision(), migration.statement());
        }

        assertEquals(List.of(10, 20), migrations.stream()
                .map(DdlMigrationService.MigrationBlock::revision).toList());
        final ArgumentCaptor<Integer> revisions = ArgumentCaptor.forClass(Integer.class);
        verify(history, times(2)).setInt(eq(2), revisions.capture());
        assertEquals(List.of(10, 20), revisions.getAllValues());
        verify(firstStatement).execute("INSERT INTO example(value) VALUES (10)");
        verify(secondStatement).execute("INSERT INTO example(value) VALUES (20)");
        verify(conn, times(2)).commit();
    }

    @Test
    public void revisionSequenceAllowsGaps() throws Exception {
        final List<DdlMigrationService.MigrationBlock> migrations = new DdlMigrationService().readMigrations(reader(
                "-- @@ 10\nSELECT 1\n-- @@ 20\nSELECT 2"));

        assertEquals(List.of(10, 20), migrations.stream()
                .map(DdlMigrationService.MigrationBlock::revision).toList());
    }

    @Test
    public void revisionSequenceRejectsDuplicatesButKeepsDescendingMarkers() throws Exception {
        final DdlMigrationService service = new DdlMigrationService();

        final List<DdlMigrationService.MigrationBlock> migrations = service.readMigrations(
                reader("-- @@ 10\nSELECT 1\n-- @@ 10\nSELECT 2\n-- @@ 5\nSELECT 3"));

        assertEquals(List.of(10, 5), migrations.stream()
                .map(DdlMigrationService.MigrationBlock::revision).toList());
        assertEquals(List.of("SELECT 1", "SELECT 3"), migrations.stream()
                .map(DdlMigrationService.MigrationBlock::statement).toList());
    }

    @Test
    public void sameRevisionIsAllowedInDifferentTags() throws Exception {
        final List<DdlMigrationService.MigrationBlock> migrations =
                new DdlMigrationService().readMigrations(reader(
                        "-- @@ 10\nSELECT 'default'\n-- @@ JP 10\nSELECT 'JP'"));

        assertEquals(List.of("", "JP"), migrations.stream()
                .map(DdlMigrationService.MigrationBlock::tag).toList());
        assertEquals(List.of(10, 10), migrations.stream()
                .map(DdlMigrationService.MigrationBlock::revision).toList());
    }

    @Test
    public void legacyNumberingIsReconciledByExactSql() throws Exception {
        final DdlMigrationService service = new DdlMigrationService();
        final List<DdlMigrationService.MigrationBlock> migrations = service.readMigrations(reader(
                "-- @@ 10\nSELECT 10\n-- @@ 20\nSELECT 20"));
        final Connection conn = historyConnection(
                new int[] {10, 11}, new String[] {"SELECT 10", "SELECT 20"});

        final Map<String, Integer> effectiveRevision = service.reconcileLegacyRevisionNumbers(
                conn, Map.of("", 11), migrations);

        assertEquals(20, effectiveRevision.get(""));
    }

    @Test
    public void reconciliationWarnsAndKeepsNumericRevisionForUnknownHistoricalSql() throws Exception {
        final DdlMigrationService service = new DdlMigrationService();
        final List<DdlMigrationService.MigrationBlock> migrations = service.readMigrations(reader(
                "-- @@ 10\nSELECT 10\n-- @@ 20\nSELECT 20"));
        final Connection conn = historyConnection(
                new int[] {10, 11}, new String[] {"SELECT 10", "SELECT changed"});

        assertEquals(11, service.reconcileLegacyRevisionNumbers(
                conn, Map.of("", 11), migrations).get(""));
    }

    @Test
    public void reconciliationWarnsAndKeepsNumericRevisionForAmbiguousRepeatedSql() throws Exception {
        final DdlMigrationService service = new DdlMigrationService();
        final List<DdlMigrationService.MigrationBlock> migrations = service.readMigrations(reader(
                "-- @@ 10\nSELECT 1\n-- @@ 20\nSELECT 1"));
        final Connection conn = historyConnection(new int[] {11}, new String[] {"SELECT 1"});

        assertEquals(11, service.reconcileLegacyRevisionNumbers(
                conn, Map.of("", 11), migrations).get(""));
    }

    @Test
    public void ambiguousRevision1495DoesNotPreventRevision1496FromRunning() throws Exception {
        final DdlMigrationService service = new DdlMigrationService();
        final List<DdlMigrationService.MigrationBlock> migrations = service.readMigrations(reader(
                "-- @@ 1495\nUPDATE expected_sql SET value = 1\n"
                + "-- @@ 1496\nINSERT INTO example(value) VALUES (1496)"));
        final Connection conn = mock(Connection.class);
        final Statement historyQuery = mock(Statement.class);
        final Statement revision1496 = mock(Statement.class);
        final ResultSet history = mock(ResultSet.class);
        final PreparedStatement historyInsert = mock(PreparedStatement.class);
        when(conn.createStatement()).thenReturn(historyQuery, revision1496);
        when(historyQuery.executeQuery("SELECT revision, statement FROM jpm_ddl_migration "
                + "WHERE tag = '' ORDER BY id"))
                .thenReturn(history);
        when(history.next()).thenReturn(true, false);
        when(history.getInt(1)).thenReturn(1495);
        when(history.getString(2)).thenReturn(
                "UPDATE jpm_sysparam SET param_value = 'historical SQL not present in script'");
        when(conn.prepareStatement(contains("INSERT INTO jpm_ddl_migration")))
                .thenReturn(historyInsert);

        service.applyMigrations(conn, Map.of("", 1495), migrations);

        verify(revision1496).execute("INSERT INTO example(value) VALUES (1496)");
        verify(historyInsert).setString(1, "");
        verify(historyInsert).setInt(2, 1496);
        verify(conn).commit();
    }

    @Test
    public void failedRevision1495DoesNotPreventRevision1496FromRunning() throws Exception {
        final DdlMigrationService service = new DdlMigrationService();
        final List<DdlMigrationService.MigrationBlock> migrations = service.readMigrations(reader(
                "-- @@ 1495\nUPDATE broken_table SET value = 1\n"
                + "-- @@ 1496\nINSERT INTO example(value) VALUES (1496)"));
        final Connection conn = mock(Connection.class);
        final Statement historyQuery = mock(Statement.class);
        final Statement revision1495 = mock(Statement.class);
        final Statement revision1496 = mock(Statement.class);
        final ResultSet emptyHistory = mock(ResultSet.class);
        final PreparedStatement historyInsert = mock(PreparedStatement.class);
        when(conn.createStatement()).thenReturn(historyQuery, revision1495, revision1496);
        when(historyQuery.executeQuery("SELECT revision, statement FROM jpm_ddl_migration "
                + "WHERE tag = '' ORDER BY id"))
                .thenReturn(emptyHistory);
        when(emptyHistory.next()).thenReturn(false);
        when(revision1495.execute("UPDATE broken_table SET value = 1"))
                .thenThrow(new SQLException("expected failure"));
        when(conn.prepareStatement(contains("INSERT INTO jpm_ddl_migration")))
                .thenReturn(historyInsert);

        service.applyMigrations(conn, Map.of("", 1494), migrations);

        verify(revision1495).execute("UPDATE broken_table SET value = 1");
        verify(revision1496).execute("INSERT INTO example(value) VALUES (1496)");
        verify(conn).rollback();
        verify(historyInsert, times(2)).executeUpdate();
        verify(conn, times(2)).commit();
    }

    @Test
    public void malformedMarkerSkipsOnlyItsBlockAndKeepsFollowingRevisions() throws Exception {
        final List<DdlMigrationService.MigrationBlock> migrations =
                new DdlMigrationService().readMigrations(reader(
                        "-- @@ 1495\nSELECT 1495\n"
                        + "-- @@ invalid\nBROKEN SQL\n"
                        + "-- @@ 1496\nSELECT 1496"));

        assertEquals(List.of(1495, 1496), migrations.stream()
                .map(DdlMigrationService.MigrationBlock::revision).toList());
        assertEquals("SELECT 1496", migrations.get(1).statement());
    }

    @Test
    public void parserHandlesMoreThanFifteenHundredRevisionsInOnePass() throws Exception {
        final StringBuilder sql = new StringBuilder();
        for (int revision = 1; revision <= 1600; revision++) {
            sql.append("-- @@ ").append(revision).append('\n');
            sql.append("SELECT ").append(revision).append(';').append('\n');
        }

        final List<DdlMigrationService.MigrationBlock> migrations =
                new DdlMigrationService().readMigrations(reader(sql.toString()));

        assertEquals(1600, migrations.size());
        assertEquals(1600, migrations.get(1599).revision());
        assertEquals("SELECT 1600;", migrations.get(1599).statement());
    }

    @Test
    public void hibernateMappingDoesNotRequireTagBeforeSchemaInitialization() throws Exception {
        final java.lang.reflect.Field tag = DdlMigration.class.getDeclaredField("tag");

        assertTrue(tag.isAnnotationPresent(Formula.class));
        assertFalse(tag.isAnnotationPresent(Column.class));
        assertEquals(0, DdlMigration.class.getAnnotation(Table.class).uniqueConstraints().length);
    }

    @Test
    public void legacyReconciliationNeverAdvancesTaggedSequences() throws Exception {
        final DdlMigrationService service = new DdlMigrationService();
        final List<DdlMigrationService.MigrationBlock> migrations = service.readMigrations(reader(
                "-- @@ JP 10\nSELECT 'moved'\n-- @@ JP 20\nSELECT 'new'"));
        final Connection conn = mock(Connection.class);
        final Statement query = mock(Statement.class);
        final ResultSet emptyHistory = mock(ResultSet.class);
        when(conn.createStatement()).thenReturn(query);
        when(query.executeQuery("SELECT revision, statement FROM jpm_ddl_migration "
                + "WHERE tag = '' ORDER BY id")).thenReturn(emptyHistory);
        when(emptyHistory.next()).thenReturn(false);

        final Map<String, Integer> effective = service.reconcileLegacyRevisionNumbers(
                conn, Map.of("", 2185, "JP", 10), migrations);

        assertEquals(10, effective.get("JP"));
        assertEquals(2185, effective.get(""));
    }

    @Test
    public void parserSupportsIndependentCaseInsensitiveTags() throws Exception {
        final List<DdlMigrationService.MigrationBlock> migrations =
                new DdlMigrationService().readMigrations(reader(
                        "-- @@ 2185\nSELECT 'default 2185'\n"
                        + "-- @@ jp 210\nSELECT 'JP 210'\n"
                        + "-- @@ 2186\nSELECT 'default 2186'\n"
                        + "-- @@ JP 211\nSELECT 'JP 211'"));

        assertEquals(List.of("", "JP", "", "JP"), migrations.stream()
                .map(DdlMigrationService.MigrationBlock::tag).toList());
        assertEquals(List.of(2185, 210, 2186, 211), migrations.stream()
                .map(DdlMigrationService.MigrationBlock::revision).toList());
    }

    @Test
    public void malformedTaggedMarkerSkipsOnlyItsBlock() throws Exception {
        final List<DdlMigrationService.MigrationBlock> migrations =
                new DdlMigrationService().readMigrations(reader(
                        "-- @@ JP 1\nSELECT 1\n"
                        + "-- @@ invalid.tag 2\nBROKEN SQL\n"
                        + "-- @@ jp 3\nSELECT 3"));

        assertEquals(List.of(1, 3), migrations.stream()
                .map(DdlMigrationService.MigrationBlock::revision).toList());
        assertEquals(List.of("JP", "JP"), migrations.stream()
                .map(DdlMigrationService.MigrationBlock::tag).toList());
    }

    @Test
    public void pendingRevisionsAreIndependentPerTagAndKeepFileOrder() throws Exception {
        final DdlMigrationService service = new DdlMigrationService();
        final List<DdlMigrationService.MigrationBlock> migrations = service.readMigrations(reader(
                "-- @@ 2185\nSELECT 2185\n"
                + "-- @@ JP 210\nSELECT 210\n"
                + "-- @@ 2186\nSELECT 2186\n"
                + "-- @@ jp 211\nSELECT 211"));
        final Connection conn = mock(Connection.class);
        final Statement historyQuery = mock(Statement.class);
        final Statement default2185 = mock(Statement.class);
        final Statement default2186 = mock(Statement.class);
        final Statement jp211 = mock(Statement.class);
        final ResultSet emptyHistory = mock(ResultSet.class);
        final PreparedStatement historyInsert = mock(PreparedStatement.class);
        when(conn.createStatement()).thenReturn(
                historyQuery, default2185, default2186, jp211);
        when(historyQuery.executeQuery(
                "SELECT revision, statement FROM jpm_ddl_migration "
                + "WHERE tag = '' ORDER BY id"))
                .thenReturn(emptyHistory);
        when(emptyHistory.next()).thenReturn(false);
        when(conn.prepareStatement(contains("INSERT INTO jpm_ddl_migration")))
                .thenReturn(historyInsert);

        service.applyMigrations(conn, Map.of("", 2184, "JP", 210), migrations);

        final InOrder order = inOrder(default2185, default2186, jp211);
        order.verify(default2185).execute("SELECT 2185");
        order.verify(default2186).execute("SELECT 2186");
        order.verify(jp211).execute("SELECT 211");
        verify(historyInsert, times(2)).setString(1, "");
        verify(historyInsert).setString(1, "JP");
        verify(historyInsert, times(3)).executeUpdate();
    }

    @Test
    public void legacyHistoryTableIsUpgradedInPlace() throws Exception {
        final Connection conn = mock(Connection.class);
        final Statement create = mock(Statement.class);
        final Statement alter = mock(Statement.class);
        final DatabaseMetaData metadata = mock(DatabaseMetaData.class);
        final ResultSet lowerColumns = mock(ResultSet.class);
        final ResultSet upperColumns = mock(ResultSet.class);
        final ResultSet indexes = mock(ResultSet.class);
        when(conn.createStatement()).thenReturn(create, alter);
        when(conn.getMetaData()).thenReturn(metadata);
        when(conn.getCatalog()).thenReturn("database");
        when(metadata.getColumns("database", null, "jpm_ddl_migration", null))
                .thenReturn(lowerColumns);
        when(metadata.getColumns("database", null, "JPM_DDL_MIGRATION", null))
                .thenReturn(upperColumns);
        when(lowerColumns.next()).thenReturn(false);
        when(upperColumns.next()).thenReturn(false);
        when(metadata.getIndexInfo("database", null, "jpm_ddl_migration", true, false))
                .thenReturn(indexes);
        when(indexes.next()).thenReturn(true, false);
        when(indexes.getString("INDEX_NAME")).thenReturn("jpm_ddl_migration_revision_uq");
        when(indexes.getString("COLUMN_NAME")).thenReturn("revision");
        when(indexes.getShort("ORDINAL_POSITION")).thenReturn((short) 1);

        new DdlMigrationService().ensureTable(conn);

        verify(create).execute(contains("tag VARCHAR(64) NOT NULL DEFAULT ''"));
        verify(alter).execute("ALTER TABLE jpm_ddl_migration "
                + "ADD COLUMN tag VARCHAR(64) NOT NULL DEFAULT '' AFTER id, "
                + "DROP INDEX `jpm_ddl_migration_revision_uq`, "
                + "ADD UNIQUE KEY jpm_ddl_migration_tag_revision_uq (tag, revision)");
    }

    @Test
    public void currentHistoryTableNeedsNoAlter() throws Exception {
        final Connection conn = mock(Connection.class);
        final Statement create = mock(Statement.class);
        final DatabaseMetaData metadata = mock(DatabaseMetaData.class);
        final ResultSet columns = mock(ResultSet.class);
        final ResultSet indexes = mock(ResultSet.class);
        when(conn.createStatement()).thenReturn(create);
        when(conn.getMetaData()).thenReturn(metadata);
        when(conn.getCatalog()).thenReturn("database");
        when(metadata.getColumns("database", null, "jpm_ddl_migration", null))
                .thenReturn(columns);
        when(columns.next()).thenReturn(true, false);
        when(columns.getString("COLUMN_NAME")).thenReturn("tag");
        when(metadata.getIndexInfo("database", null, "jpm_ddl_migration", true, false))
                .thenReturn(indexes);
        when(indexes.next()).thenReturn(true, true, false);
        when(indexes.getString("INDEX_NAME"))
                .thenReturn("jpm_ddl_migration_tag_revision_uq");
        when(indexes.getString("COLUMN_NAME")).thenReturn("tag", "revision");
        when(indexes.getShort("ORDINAL_POSITION")).thenReturn((short) 1, (short) 2);

        new DdlMigrationService().ensureTable(conn);

        verify(create, never()).execute(contains("ALTER TABLE"));
    }

    private Connection lockTimeoutConnection(boolean autoCommit) throws Exception {
        final Connection conn = mock(Connection.class);
        final PreparedStatement lock = mock(PreparedStatement.class);
        final ResultSet result = mock(ResultSet.class);
        when(conn.getAutoCommit()).thenReturn(autoCommit);
        when(conn.prepareStatement("SELECT GET_LOCK(?, ?)")).thenReturn(lock);
        when(lock.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(true);
        when(result.getInt(1)).thenReturn(0);
        when(result.wasNull()).thenReturn(false);
        return conn;
    }

    private Connection historyConnection(int[] revisions, String[] statements) throws Exception {
        final Connection conn = mock(Connection.class);
        final Statement query = mock(Statement.class);
        final ResultSet result = mock(ResultSet.class);
        when(conn.createStatement()).thenReturn(query);
        when(query.executeQuery("SELECT revision, statement FROM jpm_ddl_migration "
                + "WHERE tag = '' ORDER BY id"))
                .thenReturn(result);

        final Boolean[] next = new Boolean[revisions.length + 1];
        for (int i = 0; i < revisions.length; i++) {
            next[i] = true;
        }
        next[revisions.length] = false;
        when(result.next()).thenReturn(next[0], java.util.Arrays.copyOfRange(next, 1, next.length));

        final Integer[] revisionValues = java.util.Arrays.stream(revisions).boxed().toArray(Integer[]::new);
        when(result.getInt(1)).thenReturn(
                revisionValues[0], java.util.Arrays.copyOfRange(revisionValues, 1, revisionValues.length));
        when(result.getString(2)).thenReturn(
                statements[0], java.util.Arrays.copyOfRange(statements, 1, statements.length));
        return conn;
    }

    private BufferedReader reader(String sql) {
        return new BufferedReader(new StringReader(sql));
    }
}
