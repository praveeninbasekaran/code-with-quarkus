@ApplicationScoped
public class AlertDashboardService {

    @Inject
    AlertDashboardViewRepository viewRepository;

    public List<AlertDashboardDto> getAllAlertsForDashboard() {
        List<AlertDashboardView> views = viewRepository.findAllDashboardAlerts();
        if (views.isEmpty()) {
            throw new NotFoundException("No alerts found.");
        }
        return views.stream()
                .map(AlertMapper::toDashboardDto)
                .toList();
    }

    public AlertDashboardDto getAlertById(Long alertId) {
        if (alertId == null || alertId <= 0) {
            throw new BadRequestException("Invalid alert ID provided.");
        }

        return viewRepository.findByAlertId(alertId)
                .map(AlertMapper::toDashboardDto)
                .orElseThrow(() -> new NotFoundException("Alert not found for ID: " + alertId));
    }
}
public List<AlertDashboardDto> getRelevantAlertsForRole(String role) {
    String query = """
        SELECT * FROM drm_sit.alert_t_alert_dashboard_relevant
        WHERE value->'workflow'->'stage_1'->'role' ? :role
           OR value->'workflow'->'stage_2'->'role' ? :role
           OR value->'workflow'->'stage_3'->'role' ? :role
        """;

    return entityManager.createNativeQuery(query, AlertDashboardView.class)
                        .setParameter("role", role)
                        .getResultList()
                        .stream()
                        .map(AlertMapper::toDashboardDto)
                        .toList();
}
