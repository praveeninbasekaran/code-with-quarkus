INSERT INTO alert_t_alert_data (
    rule_id, alert_date, type_of_alert, rule_name, rule_description, criteria,
    risk_type, rating, factor, country, assessment_unit, segment, subsegment,
    alert_status, due_date, latest_reminder_date, escalation_date, alert_age
) VALUES (
    1001, CURRENT_TIMESTAMP, 'Ratings', 'Rule Name 1', 'This rule checks risk changes.',
    'current_rating > previous_rating',
    'Market Risk', 'High', 'Volatility Risk', 'USA', 'Investment Banking', 'CIB', 'Retail',
    'Open', CURRENT_DATE + INTERVAL '5 days', NULL, NULL, 0
);

INSERT INTO alert_t_actions (
    alert_id, action_stage, action_taken, action_by, action_role, action_date,
    action_notes, assign_notes, findings, risk_management_action
) VALUES (
    1, 1, 'Assign to Me', 2001, 'MS Specialist', CURRENT_TIMESTAMP,
    'Taking ownership of the alert.', 'Auto-assigned from pool.',
    'Initial review underway.', 'Start monitoring trades daily.'
);
INSERT INTO alert_t_ratings_history (
    alert_id, rating_type, rating_value, rating_date
) VALUES 
(1, 'Previous', 'Medium', CURRENT_DATE - INTERVAL '6 months'),
(1, 'Current', 'High', CURRENT_DATE);
INSERT INTO alert_t_stage_decisions (
    alert_id, stage, role, decision, action_date, findings, risk_management_action
) VALUES (
    1, 1, 'MS Specialist', 'Aligned', CURRENT_TIMESTAMP,
    'Volatility has increased compared to last quarter.',
    'Escalate to second-line reviewer for deeper analysis.'
);
INSERT INTO alert_audit_history (
    alert_id, action_stage, user_role, user_id, action_taken,
    assigned_to_role, assigned_to_user, action_date, assign_notes, comments, rule_management_action
) VALUES (
    1, 1, 'MS Specialist', 2001, 'Assign to Me',
    'MS Specialist', 2001, CURRENT_TIMESTAMP,
    'Initial allocation from pool.',
    'Alert created due to spike in volatility.',
    'Monitor and re-evaluate after 7 days.'
);
