-- 1. Create sequence for audit table primary key
CREATE SEQUENCE IF NOT EXISTS drm_sit.seq_user_access_audit_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- 2. Drop existing trigger and function (safety)
DROP TRIGGER IF EXISTS trg_user_access_config_audit ON drm_sit.rcsa_user_access_configuration;
DROP FUNCTION IF EXISTS drm_sit.trg_user_access_config_audit_func();

-- 3. Create trigger function
CREATE OR REPLACE FUNCTION drm_sit.trg_user_access_config_audit_func()
RETURNS TRIGGER AS $$
BEGIN
    -- INSERT
    IF TG_OP = 'INSERT' THEN
        INSERT INTO drm_sit.rcsa_user_access_configuration_audit (
            user_access_audit_id,
            user_access_id,
            bank_id,
            role_name,
            risk_level_1,
            risk_level_2,
            risk_level_3,
            business_function_11,
            business_function_12,
            business_function_13,
            country,
            legal_entity,
            created_by,
            created_at,
            updated_by,
            updated_at,
            action
        ) VALUES (
            nextval('drm_sit.seq_user_access_audit_id'),
            NEW.user_access_id,
            NEW.bank_id,
            NEW.role_name,
            NEW.risk_level_1,
            NEW.risk_level_2,
            NEW.risk_level_3,
            NEW.business_function_11,
            NEW.business_function_12,
            NEW.business_function_13,
            NEW.country,
            NEW.legal_entity,
            NEW.created_by,
            NEW.created_at,
            NEW.updated_by,
            NEW.updated_at,
            'add'
        );
        RETURN NEW;

    -- UPDATE
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO drm_sit.rcsa_user_access_configuration_audit (
            user_access_audit_id,
            user_access_id,
            bank_id,
            role_name,
            risk_level_1,
            risk_level_2,
            risk_level_3,
            business_function_11,
            business_function_12,
            business_function_13,
            country,
            legal_entity,
            created_by,
            created_at,
            updated_by,
            updated_at,
            action
        ) VALUES (
            nextval('drm_sit.seq_user_access_audit_id'),
            NEW.user_access_id,
            NEW.bank_id,
            NEW.role_name,
            NEW.risk_level_1,
            NEW.risk_level_2,
            NEW.risk_level_3,
            NEW.business_function_11,
            NEW.business_function_12,
            NEW.business_function_13,
            NEW.country,
            NEW.legal_entity,
            NEW.created_by,
            NEW.created_at,
            NEW.updated_by,
            NEW.updated_at,
            'edit'
        );
        RETURN NEW;

    -- DELETE
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO drm_sit.rcsa_user_access_configuration_audit (
            user_access_audit_id,
            user_access_id,
            bank_id,
            role_name,
            risk_level_1,
            risk_level_2,
            risk_level_3,
            business_function_11,
            business_function_12,
            business_function_13,
            country,
            legal_entity,
            created_by,
            created_at,
            updated_by,
            updated_at,
            action
        ) VALUES (
            nextval('drm_sit.seq_user_access_audit_id'),
            OLD.user_access_id,
            OLD.bank_id,
            OLD.role_name,
            OLD.risk_level_1,
            OLD.risk_level_2,
            OLD.risk_level_3,
            OLD.business_function_11,
            OLD.business_function_12,
            OLD.business_function_13,
            OLD.country,
            OLD.legal_entity,
            OLD.created_by,
            OLD.created_at,
            OLD.updated_by,
            OLD.updated_at,
            'delete'
        );
        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- 4. Create trigger on main table
CREATE TRIGGER trg_user_access_config_audit
AFTER INSERT OR UPDATE OR DELETE
ON drm_sit.rcsa_user_access_configuration
FOR EACH ROW
EXECUTE FUNCTION drm_sit.trg_user_access_config_audit_func();