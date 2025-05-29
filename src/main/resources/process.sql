-- 1. Drop existing trigger and function (if exists)
DROP TRIGGER IF EXISTS trg_udl_process_audit
    ON drm_sit.rcsa_udl_process_management;

DROP FUNCTION IF EXISTS drm_sit.trg_udl_process_audit_func();

-- 2. Create trigger function
CREATE OR REPLACE FUNCTION drm_sit.trg_udl_process_audit_func()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO drm_sit.rcsa_process_management_audit (
            process_audit_id,
            process_id,
            process_name,
            process_description,
            process_owner,
            business_function_11,
            business_function_12,
            business_function_13,
            country,
            legal_entity,
            business_dynamics,
            process_inputs,
            process_outputs,
            process_activities,
            created_by,
            created_at,
            modified_by,
            modified_at,
            action
        ) VALUES (
            nextval('drm_sit.seq_process_audit_id'),
            NEW.process_id,
            NEW.process_name,
            NEW.process_description,
            NEW.process_owner,
            NEW.business_function_11,
            NEW.business_function_12,
            NEW.business_function_13,
            NEW.country,
            NEW.legal_entity,
            NEW.business_dynamics,
            NEW.process_inputs,
            NEW.process_outputs,
            NEW.process_activities,
            NEW.created_by,
            NEW.created_at,
            NEW.modified_by,
            NEW.modified_at,
            'add'
        );
        RETURN NEW;

    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO drm_sit.rcsa_process_management_audit (
            process_audit_id,
            process_id,
            process_name,
            process_description,
            process_owner,
            business_function_11,
            business_function_12,
            business_function_13,
            country,
            legal_entity,
            business_dynamics,
            process_inputs,
            process_outputs,
            process_activities,
            created_by,
            created_at,
            modified_by,
            modified_at,
            action
        ) VALUES (
            nextval('drm_sit.seq_process_audit_id'),
            NEW.process_id,
            NEW.process_name,
            NEW.process_description,
            NEW.process_owner,
            NEW.business_function_11,
            NEW.business_function_12,
            NEW.business_function_13,
            NEW.country,
            NEW.legal_entity,
            NEW.business_dynamics,
            NEW.process_inputs,
            NEW.process_outputs,
            NEW.process_activities,
            NEW.created_by,
            NEW.created_at,
            NEW.modified_by,
            NEW.modified_at,
            'edit'
        );
        RETURN NEW;

    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO drm_sit.rcsa_process_management_audit (
            process_audit_id,
            process_id,
            process_name,
            process_description,
            process_owner,
            business_function_11,
            business_function_12,
            business_function_13,
            country,
            legal_entity,
            business_dynamics,
            process_inputs,
            process_outputs,
            process_activities,
            created_by,
            created_at,
            modified_by,
            modified_at,
            action
        ) VALUES (
            nextval('drm_sit.seq_process_audit_id'),
            OLD.process_id,
            OLD.process_name,
            OLD.process_description,
            OLD.process_owner,
            OLD.business_function_11,
            OLD.business_function_12,
            OLD.business_function_13,
            OLD.country,
            OLD.legal_entity,
            OLD.business_dynamics,
            OLD.process_inputs,
            OLD.process_outputs,
            OLD.process_activities,
            OLD.created_by,
            OLD.created_at,
            OLD.modified_by,
            OLD.modified_at,
            'delete'
        );
        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- 3. Create new trigger on rcsa_udl_process_management
CREATE TRIGGER trg_udl_process_audit
AFTER INSERT OR UPDATE OR DELETE
ON drm_sit.rcsa_udl_process_management
FOR EACH ROW
EXECUTE FUNCTION drm_sit.trg_udl_process_audit_func();