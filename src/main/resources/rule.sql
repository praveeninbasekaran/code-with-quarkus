-- 1. Create sequence for main table (rule_id) - 6 digit starting from 100001
CREATE SEQUENCE IF NOT EXISTS drm_sit.seq_rule_id
    START WITH 100001
    INCREMENT BY 1
    MAXVALUE 999999
    CACHE 1;

-- 2. Create sequence for audit table primary key (rule_audit_id)
CREATE SEQUENCE IF NOT EXISTS drm_sit.seq_rule_audit_id
    START WITH 1
    INCREMENT BY 1
    CACHE 1;

-- 3. Create main table: rcsa_rule_management
CREATE TABLE IF NOT EXISTS drm_sit.rcsa_rule_management (
    rule_id BIGINT PRIMARY KEY DEFAULT nextval('drm_sit.seq_rule_id'),
    rule_status BIGINT,
    alert_generation_status VARCHAR(20),
    rule_name VARCHAR,
    rule_description VARCHAR(5000),
    risk_level_1 VARCHAR,
    risk_level_2 VARCHAR,
    risk_level_3 VARCHAR,
    country VARCHAR,
    legal_entity VARCHAR,
    business_function_l1 VARCHAR,
    business_function_l2 VARCHAR,
    business_function_l3 VARCHAR,
    process_id VARCHAR,
    rule_entitlement_stage_1 VARCHAR,
    rule_entitlement_stage_2 VARCHAR,
    rule_entitlement_stage_3 VARCHAR,
    category_of_notification VARCHAR,
    expected_risk_response_stage_1 VARCHAR,
    expected_risk_response_stage_2 VARCHAR,
    expected_risk_response_stage_3 VARCHAR,
    stage_due_in_days INTEGER,
    due_in_days INTEGER,
    email_template_alert_content VARCHAR,
    comments VARCHAR(5000),
    created_by VARCHAR,
    created_date TIMESTAMP,
    updated_by VARCHAR,
    updated_date TIMESTAMP
);

-- 4. Create audit table: rcsa_rule_management_audit
CREATE TABLE IF NOT EXISTS drm_sit.rcsa_rule_management_audit (
    rule_audit_id BIGINT PRIMARY KEY,
    rule_id BIGINT,
    rule_status BIGINT,
    alert_generation_status VARCHAR(20),
    rule_name VARCHAR,
    rule_description VARCHAR(5000),
    risk_level_1 VARCHAR,
    risk_level_2 VARCHAR,
    risk_level_3 VARCHAR,
    country VARCHAR,
    legal_entity VARCHAR,
    business_function_l1 VARCHAR,
    business_function_l2 VARCHAR,
    business_function_l3 VARCHAR,
    process_id VARCHAR,
    rule_entitlement_stage_1 VARCHAR,
    rule_entitlement_stage_2 VARCHAR,
    rule_entitlement_stage_3 VARCHAR,
    category_of_notification VARCHAR,
    expected_risk_response_stage_1 VARCHAR,
    expected_risk_response_stage_2 VARCHAR,
    expected_risk_response_stage_3 VARCHAR,
    stage_due_in_days INTEGER,
    due_in_days INTEGER,
    email_template_alert_content VARCHAR,
    comments VARCHAR(5000),
    created_by VARCHAR,
    created_date TIMESTAMP,
    updated_by VARCHAR,
    updated_date TIMESTAMP,
    action VARCHAR
);

-- 5. Drop old trigger & function (if exist)
DROP TRIGGER IF EXISTS trg_rule_management_audit ON drm_sit.rcsa_rule_management;
DROP FUNCTION IF EXISTS drm_sit.trg_rule_management_audit_func();

-- 6. Create trigger function
CREATE OR REPLACE FUNCTION drm_sit.trg_rule_management_audit_func()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO drm_sit.rcsa_rule_management_audit
        SELECT nextval('drm_sit.seq_rule_audit_id'), NEW.*, 'add';
        RETURN NEW;

    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO drm_sit.rcsa_rule_management_audit
        SELECT nextval('drm_sit.seq_rule_audit_id'), NEW.*, 'edit';
        RETURN NEW;

    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO drm_sit.rcsa_rule_management_audit
        SELECT nextval('drm_sit.seq_rule_audit_id'), OLD.*, 'delete';
        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- 7. Create trigger on main table
CREATE TRIGGER trg_rule_management_audit
AFTER INSERT OR UPDATE OR DELETE
ON drm_sit.rcsa_rule_management
FOR EACH ROW
EXECUTE FUNCTION drm_sit.trg_rule_management_audit_func();