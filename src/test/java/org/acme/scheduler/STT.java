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
        // Should not throw any error and simply return
    }
}