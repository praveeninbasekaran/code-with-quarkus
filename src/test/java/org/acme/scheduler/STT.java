@Test
void testUpdateEmailStatusFailed_withNullId_shouldLogToConsole() throws SQLException {
    // Capture System.out
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(outputStream));

    // Act
    schedulerTasks.updateEmailStatusFailed(null, "Failed");

    // Restore original System.out
    System.setOut(originalOut);
    String outputLogs = outputStream.toString();

    // Assert log presence
    assertTrue(outputLogs.contains("No EmailRequestIds provided for update"), 
               "Expected log message not found for null emailRequestId");
}

@Test
void testUpdateEmailStatusFailed_withValidId_shouldExecuteUpdate() throws Exception {
    Long requestId = 101L;

    // Mock DataSource and PreparedStatement behavior
    DataSource mockDataSource = Mockito.mock(DataSource.class);
    Connection mockConnection = Mockito.mock(Connection.class);
    PreparedStatement mockPreparedStatement = Mockito.mock(PreparedStatement.class);

    // Return values for mocks
    when(mockDataSource.getConnection()).thenReturn(mockConnection);
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeUpdate()).thenReturn(1);

    // Temporarily override Arc.container().instance(DataSource.class).get() using Reflection
    Field dataSourceField = SchedulerTasks.class.getDeclaredField("dataSource");
    dataSourceField.setAccessible(true);
    dataSourceField.set(schedulerTasks, mockDataSource);

    // Act
    schedulerTasks.updateEmailStatusFailed(requestId, "Failed");

    // Verify behavior
    verify(mockPreparedStatement).setString(eq(1), eq("Failed"));
    verify(mockPreparedStatement).setTimestamp(eq(2), any());
    verify(mockPreparedStatement).setLong(eq(3), eq(requestId));
    verify(mockPreparedStatement).executeUpdate();
}

@Test
void testUpdateEmailStatusFailed_whenSQLException_shouldLogErrorToConsole() throws Exception {
    Long requestId = 102L;

    DataSource mockDataSource = Mockito.mock(DataSource.class);
    when(mockDataSource.getConnection()).thenThrow(new SQLException("Simulated DB failure"));

    // Inject mock DataSource via reflection (since Arc.container().instance(...) is used internally)
    Field dataSourceField = SchedulerTasks.class.getDeclaredField("dataSource");
    dataSourceField.setAccessible(true);
    dataSourceField.set(schedulerTasks, mockDataSource);

    // Capture error output
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream originalErr = System.err;
    System.setErr(new PrintStream(outputStream));

    // Act
    schedulerTasks.updateEmailStatusFailed(requestId, "Failed");

    // Restore
    System.setErr(originalErr);
    String errorLogs = outputStream.toString();

    assertTrue(errorLogs.contains("Error performing bulk update in email_scheduler table"),
               "Expected error log not found");
}