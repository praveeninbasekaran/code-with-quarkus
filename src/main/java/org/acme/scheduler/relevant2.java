@ApplicationScoped
public class RuleService {

    private static final Logger LOGGER = Logger.getLogger(RuleService.class);

    @Inject
    EntityManager entityManager;

    /**
     * Fetches rule IDs from rule_t_rule_data where any of the workflow stage roles match the input roles.
     *
     * @param roles list of role names to match in the JSONB workflow
     * @return list of matching rule IDs
     */
    public List<Integer> getRuleIdsForRoles(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            LOGGER.warn("Input role list is null or empty");
            throw new BadRequestException("Role list must not be null or empty.");
        }

        String sql = """
            SELECT rule_id
            FROM drm_sit.rule_t_rule_data
            WHERE EXISTS (
                SELECT 1 FROM jsonb_array_elements_text(value->'workflow'->'stage_1'->'role') AS r(role)
                WHERE r.role = ANY(:roles)
            )
            OR EXISTS (
                SELECT 1 FROM jsonb_array_elements_text(value->'workflow'->'stage_2'->'role') AS r(role)
                WHERE r.role = ANY(:roles)
            )
            OR EXISTS (
                SELECT 1 FROM jsonb_array_elements_text(value->'workflow'->'stage_3'->'role') AS r(role)
                WHERE r.role = ANY(:roles)
            )
            """;

        try {
            @SuppressWarnings("unchecked")
            List<Integer> ruleIds = entityManager.createNativeQuery(sql)
                .setParameter("roles", roles)
                .getResultList();

            if (ruleIds.isEmpty()) {
                LOGGER.info("No rule IDs found for roles: " + roles);
            }

            return ruleIds;
        } catch (PersistenceException e) {
            LOGGER.error("Failed to fetch rule IDs for roles: " + roles, e);
            throw new InternalServerErrorException("Database error occurred while fetching rule IDs.");
        }
    }
}