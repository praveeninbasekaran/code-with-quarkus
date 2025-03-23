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