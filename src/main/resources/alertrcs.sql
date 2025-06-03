CREATE TABLE rcsa_alert_dashboard (
    alert_id                BIGINT PRIMARY KEY,
    rule_id                 BIGINT NOT NULL,
    alert_date              TIMESTAMP NOT NULL,
    type_of_alert           VARCHAR(50),     -- Rating / Indicator / Adhoc RCSA
    rule_name               VARCHAR(255),
    rule_description        TEXT,
    rule_criteria_id        BIGINT,
    process_name            VARCHAR(255),
    risk_name               VARCHAR(255),
    inherent_risk           VARCHAR(100),
    control_effectiveness   VARCHAR(100),
    previous_risk_rating    VARCHAR(100),
    calculated_risk_rating  VARCHAR(100),
    latest_risk_rating      VARCHAR(100),
    assigned_role           VARCHAR(100),     -- Role or persona assigned
    assigned_to             VARCHAR(255),     -- e.g. John, Doe (1234567)
    alert_status            VARCHAR(50),      -- Open / Closed / Overdue / etc.
    due_date                TIMESTAMP,
    
    created_date            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date            TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);