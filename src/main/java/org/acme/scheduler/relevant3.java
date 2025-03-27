@ApplicationScoped
public class AlertDashboardService {

    @Inject
    EntityManager entityManager;

    /**
     * Fetches paginated alerts from alert_t_alert_data based on rule IDs
     * where the given role is present in any workflow stage.
     *
     * @param roleName user role (e.g., "MS Checker")
     * @param page     page number (0-based index)
     * @param size     page size
     * @return list of AlertDashboardDto
     */
    public List<AlertDashboardDto> getAlertsByRelevantRole(String roleName, int page, int size) {
        if (roleName == null || roleName.isBlank()) {
            throw new BadRequestException("Role name must not be null or empty.");
        }

        String ruleIdSql = """
            SELECT rule_id
            FROM drm_sit.rule_t_rule_data
            WHERE EXISTS (
                SELECT 1 FROM jsonb_array_elements_text(value->'workflow'->'stage_1'->'role') AS r(role)
                WHERE r.role = :role
            )
            OR EXISTS (
                SELECT 1 FROM jsonb_array_elements_text(value->'workflow'->'stage_2'->'role') AS r(role)
                WHERE r.role = :role
            )
            OR EXISTS (
                SELECT 1 FROM jsonb_array_elements_text(value->'workflow'->'stage_3'->'role') AS r(role)
                WHERE r.role = :role
            )
            """;

        @SuppressWarnings("unchecked")
        List<Integer> ruleIds = entityManager.createNativeQuery(ruleIdSql)
                .setParameter("role", roleName)
                .getResultList();

        if (ruleIds.isEmpty()) {
            throw new NotFoundException("No relevant alerts found for role: " + roleName);
        }

        String alertSql = """
            SELECT *
            FROM drm_sit.alert_t_alert_dashboard_all
            WHERE rule_id = ANY(:ruleIds)
            OFFSET :offset
            LIMIT :limit
            """;

        int offset = Math.max(0, page) * Math.max(1, size);

        @SuppressWarnings("unchecked")
        List<AlertDashboardView> alerts = entityManager
                .createNativeQuery(alertSql, AlertDashboardView.class)
                .setParameter("ruleIds", ruleIds)
                .setParameter("offset", offset)
                .setParameter("limit", size)
                .getResultList();

        return alerts.stream()
                .map(AlertMapper::toDashboardDto)
                .toList();
    }
}