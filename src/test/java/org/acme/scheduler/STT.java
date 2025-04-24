@QuarkusTest
class SchedulerTasksTest {

    @InjectMock
    EmailSchedulerRepository emailSchedulerRepository;

    @InjectMock
    EmailNotificationService emailNotificationService;

    @InjectMock
    ObjectMapper objectMapper;

    @InjectSpy
    SchedulerTasks schedulerTasks;

    @Test
    void testProcessPendingEmails_withValidData() throws Exception {
        // Mock EmailScheduler list
        EmailScheduler email = new EmailScheduler();
        email.setEmailRequestId(101L);
        email.setDtoJson("{\"emailFunctionality\": \"initiate_ra\"}");

        Mockito.when(emailSchedulerRepository.findPendingEmails())
               .thenReturn(List.of(email));

        EmailDto emailDto = new EmailDto();
        emailDto.setEmailFunctionality("initiate_ra");

        Mockito.when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(EmailDto.class)))
               .thenReturn(emailDto);

        Mockito.when(emailNotificationService.sendEmailRiskAssessmentInitiated(Mockito.any(EmailDto.class)))
               .thenReturn("SUCCESS");

        Mockito.doNothing().when(schedulerTasks)
                .updateEmailStatus(Mockito.anyList(), Mockito.anyString());

        schedulerTasks.processPendingEmails();

        Mockito.verify(emailSchedulerRepository).findPendingEmails();
        Mockito.verify(emailNotificationService).sendEmailRiskAssessmentInitiated(Mockito.any(EmailDto.class));
        Mockito.verify(schedulerTasks).updateEmailStatus(Mockito.anyList(), Mockito.eq("InProgress"));
    }

    @Test
    void testEmailReTrigger_whenRetryCountBelowLimit() throws Exception {
        EmailScheduler email = new EmailScheduler();
        email.setEmailRequestId(102L);
        email.setRetryCount(1L);

        Mockito.when(emailSchedulerRepository.findById(Mockito.anyLong()))
               .thenReturn(email);

        SchedulerTasks.updateEmailReTryCount(email, "Pending");

        Assertions.assertEquals(2L, email.getRetryCount());
    }

    @Test
    void testEmailReTrigger_whenRetryCountExceedsLimit() throws Exception {
        EmailScheduler email = new EmailScheduler();
        email.setEmailRequestId(103L);
        email.setRetryCount(2L);

        Mockito.when(emailSchedulerRepository.findById(Mockito.anyLong()))
               .thenReturn(email);

        SchedulerTasks.updateEmailReTryCount(email, "Failed");

        Assertions.assertEquals(3L, email.getRetryCount());
    }

    @Test
    void testUpdateEmailStatus_withEmptyList() throws Exception {
        schedulerTasks.updateEmailStatus(Collections.emptyList(), "Pending");
        // Should not throw any error and simply return
    }

    @Test
    void testUpdateEmailStatusFailed_withNullId() throws Exception {
        schedulerTasks.updateEmailStatusFailed(null, "Failed");
        // Should not throw any error and simply @Test
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


