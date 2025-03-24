CREATE OR REPLACE VIEW drm_sit.alert_t_alert_dashboard_relevant AS
SELECT 
    atad.alert_id,
    atad.alert_date,
    atad.type_of_alert,
    atad.rule_name,
    atad.rule_description,
    atad.risk_type,
    atad.rating,
    atad.factor,
    atad.country,
    atad.assessment_unit,
    atad.segment,
    atad.subsegment,
    COALESCE(ata.action_role, '') AS assigned_role,
    COALESCE(ata.action_by::TEXT, '') AS assigned_to,
    atad.alert_status,
    COALESCE(ata.action_taken, '') AS last_action_taken,
    COALESCE(ata.action_date::TEXT, '') AS last_action_date,
    atad.due_date,
    atad.latest_reminder_date,
    atad.escalation_date,
    atad.alert_age,
    rs.value -- Keep this to filter roles from JSON
FROM drm_sit.alert_t_alert_data atad
LEFT JOIN (
    SELECT DISTINCT ON (alert_id)
           alert_id,
           action_taken,
           action_date,
           action_by,
           action_role
    FROM drm_sit.alert_t_actions
    ORDER BY alert_id, action_date DESC
) ata ON atad.alert_id = ata.alert_id
JOIN drm_sit.rule_t_rule_data_stage rs ON rs.rule_id = atad.rule_id;