CREATE TABLE alert_t_alert_data (
    alert_id SERIAL PRIMARY KEY,
    rule_id INT,  -- Reference to rules table (foreign key can be added later)
    alert_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    type_of_alert VARCHAR(255) NOT NULL,
    rule_name VARCHAR(255) NOT NULL,
    rule_description TEXT,
    criteria TEXT,
    risk_type VARCHAR,
    rating VARCHAR,
    factor VARCHAR,
    country VARCHAR,
    assessment_unit VARCHAR,
    segment VARCHAR,
    subsegment VARCHAR,
    alert_status VARCHAR(50),
    due_date TIMESTAMP,
    latest_reminder_date TIMESTAMP,
    escalation_date TIMESTAMP,
    alert_age INT
);
CREATE TABLE alert_t_actions (
    action_id SERIAL PRIMARY KEY,
    alert_id INT REFERENCES alert_t_alert_data(alert_id) ON DELETE CASCADE,
    action_stage INT,
    action_taken VARCHAR(255),
    action_by INT,  -- User ID (currently decoupled from user table)
    action_role VARCHAR(255),
    action_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    action_notes VARCHAR(1000),
    assign_notes VARCHAR(1000),
    findings TEXT CHECK (char_length(findings) <= 5000),
    risk_management_action TEXT CHECK (char_length(risk_management_action) <= 5000)
);

CREATE TABLE alert_t_ratings_history (
    rating_id SERIAL PRIMARY KEY,
    alert_id INT REFERENCES alert_t_alert_data(alert_id) ON DELETE CASCADE,
    rating_type VARCHAR(50),  -- 'Current' or 'Previous'
    rating_value VARCHAR(50) NOT NULL,
    rating_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE alert_t_stage_decisions (
    decision_id SERIAL PRIMARY KEY,
    alert_id INT REFERENCES alert_t_alert_data(alert_id) ON DELETE CASCADE,
    stage INT CHECK (stage BETWEEN 1 AND 3),
    role VARCHAR(255) NOT NULL,
    decision VARCHAR(255),
    action_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    findings TEXT CHECK (char_length(findings) <= 5000),
    risk_management_action TEXT CHECK (char_length(risk_management_action) <= 5000)
);

CREATE TABLE alert_audit_history (
    audit_id SERIAL PRIMARY KEY,
    alert_id INT REFERENCES alert_t_alert_data(alert_id) ON DELETE CASCADE,
    action_stage INT CHECK (action_stage BETWEEN 1 AND 3),
    user_role VARCHAR(255),
    user_id INT,
    action_taken VARCHAR(255),
    assigned_to_role VARCHAR(255),
    assigned_to_user INT,
    action_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assign_notes TEXT,
    comments TEXT CHECK (char_length(comments) <= 5000),
    rule_management_action TEXT CHECK (char_length(rule_management_action) <= 5000)
);




