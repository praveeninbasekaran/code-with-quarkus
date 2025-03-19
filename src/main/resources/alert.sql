-- =====================================
-- 1. RULES TABLE (Reference for Alerts)
-- =====================================
CREATE TABLE rules (
    rule_id SERIAL PRIMARY KEY,
    rule_name VARCHAR(255) NOT NULL,
    rule_description TEXT
);

-- =====================================
-- 2. ALERTS TABLE (Main Alert Metadata)
-- =====================================
CREATE TABLE alerts (
    alert_id SERIAL PRIMARY KEY,
    rule_id INT REFERENCES rules(rule_id) ON DELETE CASCADE,
    alert_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    type_of_alert VARCHAR(255) NOT NULL,
    rule_name VARCHAR(255) NOT NULL,
    rule_description TEXT,
    criteria TEXT, -- Logical condition that triggered the alert
    risk_type_id INT REFERENCES risk_types(risk_type_id),
    rating_id INT REFERENCES risk_ratings(rating_id),
    factor_id INT REFERENCES risk_factors(factor_id),
    country_id INT REFERENCES countries(country_id),
    assessment_unit_id INT REFERENCES assessment_units(unit_id),
    segment_id INT REFERENCES segments(segment_id),
    subsegment_id INT REFERENCES subsegments(subsegment_id),
    assigned_role VARCHAR(255),
    assigned_to INT REFERENCES users(user_id),
    alert_status VARCHAR(50) CHECK (alert_status IN ('Open', 'In Progress', 'Closed', 'Escalated')),
    last_action_taken VARCHAR(255),
    last_action_date TIMESTAMP,
    due_date TIMESTAMP,
    latest_reminder_date TIMESTAMP,
    escalation_date TIMESTAMP,
    alert_age INT
);

-- =====================================
-- 3. ALERT RATINGS HISTORY TABLE
-- =====================================
CREATE TABLE alert_ratings_history (
    rating_id SERIAL PRIMARY KEY,
    alert_id INT REFERENCES alerts(alert_id) ON DELETE CASCADE,
    rating_type VARCHAR(50) CHECK (rating_type IN ('Current', 'Previous')) NOT NULL,
    rating_value VARCHAR(50) NOT NULL, -- e.g., High, Medium, Low
    rating_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================
-- 4. ALERT ACTIONS TABLE (Tracks User Actions)
-- =====================================
CREATE TABLE alert_actions (
    action_id SERIAL PRIMARY KEY,
    alert_id INT REFERENCES alerts(alert_id) ON DELETE CASCADE,
    action_stage INT CHECK (action_stage BETWEEN 1 AND 3), -- Stage 1, 2, 3
    action_taken VARCHAR(255) CHECK (action_taken IN ('Review Needed', 'Resolved', 'Approved', 'Rejected', 'Escalated', 'Reassigned')) NOT NULL,
    action_by INT REFERENCES users(user_id),
    action_role VARCHAR(255),  -- Role of the user who took action
    action_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    action_notes TEXT,
    findings TEXT CHECK (char_length(findings) <= 5000), -- Max 5000 characters
    risk_management_action TEXT CHECK (char_length(risk_management_action) <= 5000) -- Stores risk mitigation plan
);

-- =====================================
-- 5. ALERT STAGE DECISIONS TABLE
-- =====================================
CREATE TABLE alert_stage_decisions (
    decision_id SERIAL PRIMARY KEY,
    alert_id INT REFERENCES alerts(alert_id) ON DELETE CASCADE,
    stage INT CHECK (stage BETWEEN 1 AND 3), -- Stage 1, 2, 3
    role VARCHAR(255) NOT NULL,
    decision VARCHAR(255) CHECK (decision IN ('Accepted', 'Rejected', 'Escalated', 'Reassigned')),
    action_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    findings TEXT CHECK (char_length(findings) <= 5000),
    risk_management_action TEXT CHECK (char_length(risk_management_action) <= 5000)
);

-- =====================================
-- 6. LOOKUP TABLES (For Dropdowns & Hierarchy)
-- =====================================
CREATE TABLE risk_types (
    risk_type_id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE risk_ratings (
    rating_id SERIAL PRIMARY KEY,
    risk_type_id INT REFERENCES risk_types(risk_type_id),
    name VARCHAR(255) NOT NULL
);

CREATE TABLE risk_factors (
    factor_id SERIAL PRIMARY KEY,
    rating_id INT REFERENCES risk_ratings(rating_id),
    name VARCHAR(255) NOT NULL
);

CREATE TABLE countries (
    country_id SERIAL PRIMARY KEY,
    factor_id INT REFERENCES risk_factors(factor_id),
    name VARCHAR(255) NOT NULL
);

CREATE TABLE assessment_units (
    unit_id SERIAL PRIMARY KEY,
    country_id INT REFERENCES countries(country_id),
    name VARCHAR(255) NOT NULL
);

CREATE TABLE segments (
    segment_id SERIAL PRIMARY KEY,
    unit_id INT REFERENCES assessment_units(unit_id),
    name VARCHAR(255) NOT NULL
);

CREATE TABLE subsegments (
    subsegment_id SERIAL PRIMARY KEY,
    segment_id INT REFERENCES segments(segment_id),
    name VARCHAR(255) NOT NULL
);

-- =====================================
-- 7. USER & ROLE MANAGEMENT TABLES
-- =====================================
CREATE TABLE roles (
    role_id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    role_id INT REFERENCES roles(role_id)
);

CREATE TABLE user_rule_mappings (
    mapping_id SERIAL PRIMARY KEY,
    rule_id INT REFERENCES rules(rule_id),
    user_id INT REFERENCES users(user_id),
    role_id INT REFERENCES roles(role_id)
);

CREATE TABLE email_templates (
    template_id SERIAL PRIMARY KEY,
    template_name VARCHAR(255) NOT NULL,
    template_content TEXT NOT NULL
);

-- ============================================
-- 8. INDEXING FOR FAST LOOKUPS & PERFORMANCE
-- ============================================
CREATE INDEX idx_alert_status ON alerts(alert_status);
CREATE INDEX idx_alert_action_stage ON alert_actions(alert_id, action_stage);
CREATE INDEX idx_alert_rating ON alert_ratings_history(alert_id, rating_date);
CREATE INDEX idx_alert_decision_stage ON alert_stage_decisions(alert_id, stage);

-- ============================================
-- 9. PARTITIONING CONFIGURATION (For Active/Inactive Alerts)
-- ============================================
CREATE TABLE alerts_active PARTITION OF alerts FOR VALUES IN ('Open', 'In Progress');
CREATE TABLE alerts_closed PARTITION OF alerts FOR VALUES IN ('Closed', 'Escalated');

10.

CREATE TABLE alert_audit_history (
    audit_id SERIAL PRIMARY KEY,
    alert_id INT REFERENCES alerts(alert_id) ON DELETE CASCADE,
    action_stage INT CHECK (action_stage BETWEEN 1 AND 3), -- Stage 1, 2, 3
    user_role VARCHAR(255),  -- Role of the user who modified the alert
    user_id INT REFERENCES users(user_id),
    action_taken VARCHAR(255) CHECK (action_taken IN ('Acknowledge', 'Assign to Me', 'Refer-back', 'Aligned', 'Not-Aligned', 'Escalated', 'Resolved')),
    assigned_to_role VARCHAR(255), -- Who the alert was reassigned to
    assigned_to_user INT REFERENCES users(user_id),
    action_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    comments TEXT CHECK (char_length(comments) <= 5000),
    rule_management_action TEXT CHECK (char_length(rule_management_action) <= 5000)
);


