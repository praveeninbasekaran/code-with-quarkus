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

---


----


-- 1. Drop existing trigger and function if they exist (safety)
DROP TRIGGER IF EXISTS trg_role_permission_audit ON drm_sit.rcsa_role_permission_config;
DROP FUNCTION IF EXISTS drm_sit.trg_role_permission_audit_func();

-- 2. Create trigger function for audit
CREATE OR REPLACE FUNCTION drm_sit.trg_role_permission_audit_func()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO drm_sit.rcsa_role_permission_config_audit (
            permission_audit_id,
            permission_id,
            role_name,
            add,
            edit,
            deactivate,
            reactivate,
            approve,
            reject,
            refer_back,
            read_only,
            created_by,
            updated_by,
            created_at,
            updated_at,
            action
        )
        VALUES (
            nextval('drm_sit.seq_rcsa_role_permission_audit_id'),
            NEW.permission_id,
            NEW.role_name,
            NEW.add,
            NEW.edit,
            NEW.deactivate,
            NEW.reactivate,
            NEW.approve,
            NEW.reject,
            NEW.refer_back,
            NEW.read_only,
            NEW.created_by,
            NEW.updated_by,
            NEW.created_at,
            NEW.updated_at,
            'add'
        );
        RETURN NEW;

    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO drm_sit.rcsa_role_permission_config_audit (
            permission_audit_id,
            permission_id,
            role_name,
            add,
            edit,
            deactivate,
            reactivate,
            approve,
            reject,
            refer_back,
            read_only,
            created_by,
            updated_by,
            created_at,
            updated_at,
            action
        )
        VALUES (
            nextval('drm_sit.seq_rcsa_role_permission_audit_id'),
            NEW.permission_id,
            NEW.role_name,
            NEW.add,
            NEW.edit,
            NEW.deactivate,
            NEW.reactivate,
            NEW.approve,
            NEW.reject,
            NEW.refer_back,
            NEW.read_only,
            NEW.created_by,
            NEW.updated_by,
            NEW.created_at,
            NEW.updated_at,
            'edit'
        );
        RETURN NEW;

    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO drm_sit.rcsa_role_permission_config_audit (
            permission_audit_id,
            permission_id,
            role_name,
            add,
            edit,
            deactivate,
            reactivate,
            approve,
            reject,
            refer_back,
            read_only,
            created_by,
            updated_by,
            created_at,
            updated_at,
            action
        )
        VALUES (
            nextval('drm_sit.seq_rcsa_role_permission_audit_id'),
            OLD.permission_id,
            OLD.role_name,
            OLD.add,
            OLD.edit,
            OLD.deactivate,
            OLD.reactivate,
            OLD.approve,
            OLD.reject,
            OLD.refer_back,
            OLD.read_only,
            OLD.created_by,
            OLD.updated_by,
            OLD.created_at,
            OLD.updated_at,
            'delete'
        );
        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- 3. Create the trigger on the main table
CREATE TRIGGER trg_role_permission_audit
AFTER INSERT OR UPDATE OR DELETE
ON drm_sit.rcsa_role_permission_config
FOR EACH ROW
EXECUTE FUNCTION drm_sit.trg_role_permission_audit_func();


---------
---------
---------

-- 1. Drop old trigger and function (safety cleanup)
DROP TRIGGER IF EXISTS trg_rule_management_audit
    ON drm_sit.rcsa_rule_management;

DROP FUNCTION IF EXISTS drm_sit.trg_rule_management_audit_func();

-- 2. Create updated audit trigger function
CREATE OR REPLACE FUNCTION drm_sit.trg_rule_management_audit_func()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO drm_sit.rcsa_rule_management_audit (
            rule_audit_id,
            rule_id,
            rule_status,
            alert_generation_status,
            rule_name,
            rule_description,
            risk_level_1,
            risk_level_2,
            risk_level_3,
            country,
            legal_entity,
            business_function_l1,
            business_function_l2,
            business_function_l3,
            process_id,
            rule_entitlement_stage_1,
            rule_entitlement_stage_2,
            rule_entitlement_stage_3,
            category_of_notification,
            expected_risk_response_stage_1,
            expected_risk_response_stage_2,
            expected_risk_response_stage_3,
            stage_due_in_days,
            due_in_days,
            email_template_alert_content,
            comments,
            action,
            created_by,
            updated_by,
            created_date,
            updated_date
        ) VALUES (
            nextval('drm_sit.seq_rule_audit_id'),
            NEW.rule_id,
            NEW.rule_status,
            NEW.alert_generation_status,
            NEW.rule_name,
            NEW.rule_description,
            NEW.risk_level_1,
            NEW.risk_level_2,
            NEW.risk_level_3,
            NEW.country,
            NEW.legal_entity,
            NEW.business_function_l1,
            NEW.business_function_l2,
            NEW.business_function_l3,
            NEW.process_id,
            NEW.rule_entitlement_stage_1,
            NEW.rule_entitlement_stage_2,
            NEW.rule_entitlement_stage_3,
            NEW.category_of_notification,
            NEW.expected_risk_response_stage_1,
            NEW.expected_risk_response_stage_2,
            NEW.expected_risk_response_stage_3,
            NEW.stage_due_in_days,
            NEW.due_in_days,
            NEW.email_template_alert_content,
            NEW.comments,
            'add',
            NEW.created_by,
            NEW.updated_by,
            NEW.created_date,
            NEW.updated_date
        );
        RETURN NEW;

    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO drm_sit.rcsa_rule_management_audit (
            rule_audit_id,
            rule_id,
            rule_status,
            alert_generation_status,
            rule_name,
            rule_description,
            risk_level_1,
            risk_level_2,
            risk_level_3,
            country,
            legal_entity,
            business_function_l1,
            business_function_l2,
            business_function_l3,
            process_id,
            rule_entitlement_stage_1,
            rule_entitlement_stage_2,
            rule_entitlement_stage_3,
            category_of_notification,
            expected_risk_response_stage_1,
            expected_risk_response_stage_2,
            expected_risk_response_stage_3,
            stage_due_in_days,
            due_in_days,
            email_template_alert_content,
            comments,
            action,
            created_by,
            updated_by,
            created_date,
            updated_date
        ) VALUES (
            nextval('drm_sit.seq_rule_audit_id'),
            NEW.rule_id,
            NEW.rule_status,
            NEW.alert_generation_status,
            NEW.rule_name,
            NEW.rule_description,
            NEW.risk_level_1,
            NEW.risk_level_2,
            NEW.risk_level_3,
            NEW.country,
            NEW.legal_entity,
            NEW.business_function_l1,
            NEW.business_function_l2,
            NEW.business_function_l3,
            NEW.process_id,
            NEW.rule_entitlement_stage_1,
            NEW.rule_entitlement_stage_2,
            NEW.rule_entitlement_stage_3,
            NEW.category_of_notification,
            NEW.expected_risk_response_stage_1,
            NEW.expected_risk_response_stage_2,
            NEW.expected_risk_response_stage_3,
            NEW.stage_due_in_days,
            NEW.due_in_days,
            NEW.email_template_alert_content,
            NEW.comments,
            'edit',
            NEW.created_by,
            NEW.updated_by,
            NEW.created_date,
            NEW.updated_date
        );
        RETURN NEW;

    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO drm_sit.rcsa_rule_management_audit (
            rule_audit_id,
            rule_id,
            rule_status,
            alert_generation_status,
            rule_name,
            rule_description,
            risk_level_1,
            risk_level_2,
            risk_level_3,
            country,
            legal_entity,
            business_function_l1,
            business_function_l2,
            business_function_l3,
            process_id,
            rule_entitlement_stage_1,
            rule_entitlement_stage_2,
            rule_entitlement_stage_3,
            category_of_notification,
            expected_risk_response_stage_1,
            expected_risk_response_stage_2,
            expected_risk_response_stage_3,
            stage_due_in_days,
            due_in_days,
            email_template_alert_content,
            comments,
            action,
            created_by,
            updated_by,
            created_date,
            updated_date
        ) VALUES (
            nextval('drm_sit.seq_rule_audit_id'),
            OLD.rule_id,
            OLD.rule_status,
            OLD.alert_generation_status,
            OLD.rule_name,
            OLD.rule_description,
            OLD.risk_level_1,
            OLD.risk_level_2,
            OLD.risk_level_3,
            OLD.country,
            OLD.legal_entity,
            OLD.business_function_l1,
            OLD.business_function_l2,
            OLD.business_function_l3,
            OLD.process_id,
            OLD.rule_entitlement_stage_1,
            OLD.rule_entitlement_stage_2,
            OLD.rule_entitlement_stage_3,
            OLD.category_of_notification,
            OLD.expected_risk_response_stage_1,
            OLD.expected_risk_response_stage_2,
            OLD.expected_risk_response_stage_3,
            OLD.stage_due_in_days,
            OLD.due_in_days,
            OLD.email_template_alert_content,
            OLD.comments,
            'delete',
            OLD.created_by,
            OLD.updated_by,
            OLD.created_date,
            OLD.updated_date
        );
        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- 3. Recreate the trigger
CREATE TRIGGER trg_rule_management_audit
AFTER INSERT OR UPDATE OR DELETE
ON drm_sit.rcsa_rule_management
FOR EACH ROW
EXECUTE FUNCTION drm_sit.trg_rule_management_audit_func();



-- Step 1: Create sequence for rcsa_rule_criteria primary key
CREATE SEQUENCE rcsa_rule_criteria_id_seq
    START WITH 1000001
    INCREMENT BY 1
    MINVALUE 1000001
    NO MAXVALUE
    CACHE 1;

-- Step 2: Create table rcsa_rule_criteria
CREATE TABLE rcsa_rule_criteria (
    rule_criteria_id     BIGINT PRIMARY KEY DEFAULT nextval('rcsa_rule_criteria_id_seq'),
    rule_id              BIGINT NOT NULL,
    logical_operator     VARCHAR(10),        -- AND / OR
    lhs_field            VARCHAR(255) NOT NULL,
    mathematical_operator VARCHAR(10),       -- =, >, <, etc.
    is_rhs_custom_value  BOOLEAN NOT NULL,
    rhs_field            VARCHAR(255),
    created_by           VARCHAR(100),
    updated_by           VARCHAR(100),
    created_date         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Step 3: Foreign key to rcsa_rule_management
    CONSTRAINT fk_rule_id FOREIGN KEY (rule_id)
        REFERENCES rcsa_rule_management(rule_id)
        ON DELETE CASCADE
);
