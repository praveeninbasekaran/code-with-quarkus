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


import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class SchedulerTasksUpdateEmailStatusFailedTest {

    @Inject
    SchedulerTasks schedulerTasks;

    @InjectMock
    DataSource dataSource;

    @Mock
    Connection mockConnection;

    @Mock
    PreparedStatement mockPreparedStatement;

    @Test
    void testUpdateEmailStatusFailed_withNullId_shouldLogAndReturn() throws SQLException {
        LogCaptor logCaptor = LogCaptor.forClass(SchedulerTasks.class);

        schedulerTasks.updateEmailStatusFailed(null, "Failed");

        assertTrue(logCaptor.getInfoLogs().stream()
                .anyMatch(log -> log.contains("No EmailRequestIds provided for update")));
    }

    @Test
    void testUpdateEmailStatusFailed_happyPath_shouldExecuteUpdate() throws Exception {
        Long requestId = 101L;

        when(dataSource.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        schedulerTasks.updateEmailStatusFailed(requestId, "Failed");

        verify(mockPreparedStatement).setString(eq(1), eq("Failed"));
        verify(mockPreparedStatement, times(1)).setTimestamp(eq(2), any(Timestamp.class));
        verify(mockPreparedStatement).setLong(eq(3), eq(requestId));
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testUpdateEmailStatusFailed_whenSQLException_shouldLogError() throws Exception {
        Long requestId = 102L;

        when(dataSource.getConnection()).thenThrow(new SQLException("DB down"));

        LogCaptor logCaptor = LogCaptor.forClass(SchedulerTasks.class);

        schedulerTasks.updateEmailStatusFailed(requestId, "Failed");

        assertTrue(logCaptor.getErrorLogs().stream()
                .anyMatch(log -> log.contains("Error performing bulk update in email_scheduler table")));
    }
}
}