package jpaoletti.jpm2.core.service;

import java.io.BufferedReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
                conn, 1, "INSERT INTO example(value) VALUES (1)");

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
                conn, 2, "UPDATE example SET value = 2");

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

        new DdlMigrationService().executeOne(conn, 3, block);

        verify(statement).execute("INSERT INTO example(value) VALUES (1);");
        verify(statement, never()).execute(contains("UPDATE"));
        verify(history).setString(2, block);
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
            service.executeOne(conn, migration.revision(), migration.statement());
        }

        assertEquals(List.of(10, 20), migrations.stream()
                .map(DdlMigrationService.MigrationBlock::revision).toList());
        final ArgumentCaptor<Integer> revisions = ArgumentCaptor.forClass(Integer.class);
        verify(history, times(2)).setInt(eq(1), revisions.capture());
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
    public void revisionSequenceRejectsDuplicatesAndDescendingMarkers() {
        final DdlMigrationService service = new DdlMigrationService();

        assertThrows(IllegalStateException.class, () -> service.readMigrations(
                reader("-- @@ 10\nSELECT 1\n-- @@ 10\nSELECT 2")));
        assertThrows(IllegalStateException.class, () -> service.readMigrations(
                reader("-- @@ 20\nSELECT 1\n-- @@ 10\nSELECT 2")));
    }

    @Test
    public void legacyNumberingIsReconciledByExactSql() throws Exception {
        final DdlMigrationService service = new DdlMigrationService();
        final List<DdlMigrationService.MigrationBlock> migrations = service.readMigrations(reader(
                "-- @@ 10\nSELECT 10\n-- @@ 20\nSELECT 20"));
        final Connection conn = historyConnection(
                new int[] {10, 11}, new String[] {"SELECT 10", "SELECT 20"});

        final int effectiveRevision = service.reconcileLegacyRevisionNumbers(conn, 11, migrations);

        assertEquals(20, effectiveRevision);
    }

    @Test
    public void reconciliationFailsClosedForUnknownHistoricalSql() throws Exception {
        final DdlMigrationService service = new DdlMigrationService();
        final List<DdlMigrationService.MigrationBlock> migrations = service.readMigrations(reader(
                "-- @@ 10\nSELECT 10\n-- @@ 20\nSELECT 20"));
        final Connection conn = historyConnection(
                new int[] {10, 11}, new String[] {"SELECT 10", "SELECT changed"});

        assertThrows(IllegalStateException.class,
                () -> service.reconcileLegacyRevisionNumbers(conn, 11, migrations));
    }

    @Test
    public void reconciliationFailsClosedForAmbiguousRepeatedSql() throws Exception {
        final DdlMigrationService service = new DdlMigrationService();
        final List<DdlMigrationService.MigrationBlock> migrations = service.readMigrations(reader(
                "-- @@ 10\nSELECT 1\n-- @@ 20\nSELECT 1"));
        final Connection conn = historyConnection(new int[] {11}, new String[] {"SELECT 1"});

        assertThrows(IllegalStateException.class,
                () -> service.reconcileLegacyRevisionNumbers(conn, 11, migrations));
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
        when(query.executeQuery("SELECT revision, statement FROM jpm_ddl_migration ORDER BY id"))
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
