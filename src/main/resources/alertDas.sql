--all
CREATE OR REPLACE VIEW drm_sit.vw_rcsa_alert_dashboard_all AS
SELECT
    am.alert_id,
    am.alert_date,
    am.type_of_alert,
    rm.rule_name,
    rm.type_of_alert AS rule_type_of_alert,
    am.process_name,
    am.risk_name,
    am.inherent_risk_rating,
    am.control_effectiveness_rating,
    am.previous_risk_rating,
    am.calculated_risk_rating,
    am.latest_risk_rating,
    am.assigned_role,
    ur.user_role_name AS assigned_role_name,
    am.assigned_to,
    am.alert_status,
    am.due_date,
    am.last_action_taken,
    am.last_action_date,
    am.latest_reminder_date,
    am.escalation_date,
    am.stage,
    am.created_date,
    am.updated_date,
    am.updated_by

    -- Add more columns as needed
FROM
    drm_sit.rcsa_alert_management am
LEFT JOIN drm_sit.rcsa_rule_management rm
    ON am.rule_id = rm.rule_id
LEFT JOIN drm_sit.rcsa_user_roles ur
    ON am.assigned_role = ur.user_role_name  -- If assigned_role is name
    -- OR ON am.assigned_role = ur.user_role_id  -- If assigned_role is ID

-- No WHERE clause: "All" filter = every alert in system
;