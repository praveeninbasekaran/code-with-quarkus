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

-- Drop old table if exists
DROP TABLE IF EXISTS drm_sit.rcsa_alert_management;

-- Create the new table as per the final spec
CREATE TABLE drm_sit.rcsa_alert_management (
    alert_id bigint NOT NULL,
    rule_id bigint NOT NULL,
    rule_version integer,

    l1_risk_id character varying(255),
    l2_risk_id character varying(255),
    l3_risk_id character varying(255),

    business_function_l1_id character varying(255),
    business_function_l2_id character varying(255),
    business_function_l3_id character varying(255),

    country character varying(255),
    legal_entity character varying(255),
    stage character varying(50),

    alert_date timestamp without time zone NOT NULL,
    type_of_alert character varying(50),
    rule_name character varying(255),
    process_name character varying(255),
    risk_name character varying(255),

    inherent_risk_rating character varying(100),
    control_effectiveness character varying(100),
    previous_risk_rating character varying(100),
    calculated_risk_rating character varying(100),
    latest_risk_rating character varying(100),
    proposed_rr character varying(100),

    assigned_role character varying(100),
    assigned_to character varying(255),
    alert_status character varying(50),
    due_date timestamp without time zone,

    created_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_by character varying(50),

    last_action_taken character varying(50),
    last_action_date timestamp without time zone,
    latest_reminder_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    escalation_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    atom_case_id character varying(100),

    CONSTRAINT rcsa_alert_management_pkey PRIMARY KEY (alert_id),
    CONSTRAINT fk_rule_id FOREIGN KEY (rule_id)
        REFERENCES drm_sit.rcsa_rule_management(rule_id)
);

ALTER TABLE drm_sit.rcsa_alert_management
    OWNER TO svc_riskview;


---------

--all
CREATE OR REPLACE VIEW vw_rcsa_alert_dashboard_all AS
SELECT
    am.alert_id,
    am.rule_id,
    rm.rule_version,
    am.l1_risk_id,
    am.l2_risk_id,
    am.l3_risk_id,
    am.business_function_l1_id,
    am.business_function_l2_id,
    am.business_function_l3_id,
    am.country,
    am.legal_entity,
    am.stage,
    am.alert_date,
    am.type_of_alert,
    am.rule_name,
    am.process_name,
    am.risk_name,
    am.inherent_risk_rating AS latest_ir,
    am.control_effectiveness AS latest_ce,
    am.previous_risk_rating AS previous_rr,
    am.proposed_rr,
    am.assigned_role,
    am.assigned_to,
    am.alert_status,
    am.due_date,
    am.created_date,
    am.updated_date,
    am.updated_by,
    am.last_action_taken,
    am.last_action_date,
    am.latest_reminder_date,
    am.escalation_date,
    am.atom_case_id
FROM
    drm_sit.rcsa_alert_management am
LEFT JOIN
    drm_sit.rcsa_rule_management rm
ON
    am.rule_id = rm.rule_id;

