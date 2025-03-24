package com.yourcompany.alerts.service;

import com.yourcompany.alerts.dto.AlertDashboardDto;
import com.yourcompany.alerts.entity.AlertDashboardView;
import com.yourcompany.alerts.mapper.AlertMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

/**
 * Service class for Alert Dashboard operations.
 */
@ApplicationScoped
public class AlertDashboardService {

    @Inject
    EntityManager entityManager;

    /**
     * Fetches all relevant alerts for the given user role.
     * An alert is considered relevant if the user’s role is mentioned
     * in any of the workflow stages of the rule that generated the alert.
     *
     * @param roleName user’s role name (e.g., "MS Checker")
     * @return list of AlertDashboardDto
     */
    public List<AlertDashboardDto> getRelevantAlertsForRole(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            throw new BadRequestException("Role name must not be empty.");
        }

        String sql = """
            SELECT * 
            FROM drm_sit.alert_t_alert_dashboard_relevant
            WHERE value->'workflow'->'stage_1'->'role' ? :role
               OR value->'workflow'->'stage_2'->'role' ? :role
               OR value->'workflow'->'stage_3'->'role' ? :role
            """;

        List<AlertDashboardView> results = entityManager
            .createNativeQuery(sql, AlertDashboardView.class)
            .setParameter("role", roleName)
            .getResultList();

        if (results.isEmpty()) {
            throw new NotFoundException("No relevant alerts found for role: " + roleName);
        }

        return results.stream()
                .map(AlertMapper::toDashboardDto)
                .toList();
    }
}

@Entity
@Immutable
@Table(name = "alert_t_alert_dashboard_relevant", schema = "drm_sit")
public class AlertDashboardView { ... }

@GET
@Path("/relevant-to-role/{role}")
public Response getRelevantAlerts(@PathParam("role") String role) {
    List<AlertDashboardDto> dtos = alertDashboardService.getRelevantAlertsForRole(role);
    return Response.ok(dtos).build();
}