-- ===============================
-- 1. SEQUENCE FOR rating_overlay_category_audit
-- ===============================
CREATE SEQUENCE IF NOT EXISTS drm_sit.seq_rating_overlay_category_audit_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- ===============================
-- 2. TRIGGER FUNCTION FOR rating_overlay_category_audit
-- ===============================
CREATE OR REPLACE FUNCTION drm_sit.trg_rating_overlay_category_audit_func()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO drm_sit.rcsa_dropdown_rating_overlay_category_audit (
            rating_overlay_category_audit_id,
            rating_overlay_category_id,
            rating_overlay_category,
            status,
            created_by,
            created_at,
            updated_by,
            updated_at,
            comments,
            action
        ) VALUES (
            nextval('drm_sit.seq_rating_overlay_category_audit_id'),
            NEW.rating_overlay_category_id,
            NEW.rating_overlay_category,
            NEW.status,
            NEW.created_by,
            NEW.created_at,
            NEW.updated_by,
            NEW.updated_at,
            NEW.comments,
            'add'
        );
        RETURN NEW;

    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO drm_sit.rcsa_dropdown_rating_overlay_category_audit (
            rating_overlay_category_audit_id,
            rating_overlay_category_id,
            rating_overlay_category,
            status,
            created_by,
            created_at,
            updated_by,
            updated_at,
            comments,
            action
        ) VALUES (
            nextval('drm_sit.seq_rating_overlay_category_audit_id'),
            NEW.rating_overlay_category_id,
            NEW.rating_overlay_category,
            NEW.status,
            NEW.created_by,
            NEW.created_at,
            NEW.updated_by,
            NEW.updated_at,
            NEW.comments,
            CASE
                WHEN OLD.status IS DISTINCT FROM NEW.status THEN
                    CASE
                        WHEN NEW.status = 'ACTIVE' THEN 'activate'
                        WHEN NEW.status = 'INACTIVE' THEN 'deactivate'
                        ELSE 'edit'
                    END
                ELSE 'edit'
            END
        );
        RETURN NEW;

    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO drm_sit.rcsa_dropdown_rating_overlay_category_audit (
            rating_overlay_category_audit_id,
            rating_overlay_category_id,
            rating_overlay_category,
            status,
            created_by,
            created_at,
            updated_by,
            updated_at,
            comments,
            action
        ) VALUES (
            nextval('drm_sit.seq_rating_overlay_category_audit_id'),
            OLD.rating_overlay_category_id,
            OLD.rating_overlay_category,
            OLD.status,
            OLD.created_by,
            OLD.created_at,
            OLD.updated_by,
            OLD.updated_at,
            OLD.comments,
            'delete'
        );
        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- DROP and CREATE TRIGGER
DROP TRIGGER IF EXISTS trg_rating_overlay_category_audit
ON drm_sit.rcsa_dropdown_rating_overlay_category;

CREATE TRIGGER trg_rating_overlay_category_audit
AFTER INSERT OR UPDATE OR DELETE
ON drm_sit.rcsa_dropdown_rating_overlay_category
FOR EACH ROW
EXECUTE FUNCTION drm_sit.trg_rating_overlay_category_audit_func();

-- ===============================
-- 3. SEQUENCE FOR reason_for_adhoc_rcsa_audit
-- ===============================


-- 1. Create Sequence
CREATE SEQUENCE IF NOT EXISTS drm_sit.seq_reason_adhoc_rcsa_audit_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- 2. Drop Trigger if Exists (safety)
DROP TRIGGER IF EXISTS trg_reason_adhoc_rcsa_audit
ON drm_sit.rcsa_dropdown_reason_adhoc_rcsa;

-- 3. Drop Function if Exists (safety)
DROP FUNCTION IF EXISTS drm_sit.trg_reason_adhoc_rcsa_audit_func();

-- 4. Create Trigger Function
CREATE OR REPLACE FUNCTION drm_sit.trg_reason_adhoc_rcsa_audit_func()
RETURNS TRIGGER AS $$
BEGIN
    -- INSERT
    IF TG_OP = 'INSERT' THEN
        INSERT INTO drm_sit.rcsa_dropdown_reason_adhoc_rcsa_audit (
            reason_audit_id,
            reason_id,
            reason,
            status,
            created_by,
            created_at,
            updated_by,
            updated_at,
            comments,
            action
        ) VALUES (
            nextval('drm_sit.seq_reason_adhoc_rcsa_audit_id'),
            NEW.reason_id,
            NEW.reason,
            NEW.status,
            NEW.created_by,
            NEW.created_at,
            NEW.updated_by,
            NEW.updated_at,
            NEW.comments,
            'add'
        );
        RETURN NEW;

    -- UPDATE
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO drm_sit.rcsa_dropdown_reason_adhoc_rcsa_audit (
            reason_audit_id,
            reason_id,
            reason,
            status,
            created_by,
            created_at,
            updated_by,
            updated_at,
            comments,
            action
        ) VALUES (
            nextval('drm_sit.seq_reason_adhoc_rcsa_audit_id'),
            NEW.reason_id,
            NEW.reason,
            NEW.status,
            NEW.created_by,
            NEW.created_at,
            NEW.updated_by,
            NEW.updated_at,
            NEW.comments,
            CASE
                WHEN OLD.status IS DISTINCT FROM NEW.status THEN
                    CASE
                        WHEN NEW.status = 'ACTIVE' THEN 'activate'
                        WHEN NEW.status = 'INACTIVE' THEN 'deactivate'
                        ELSE 'edit'
                    END
                ELSE 'edit'
            END
        );
        RETURN NEW;

    -- DELETE
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO drm_sit.rcsa_dropdown_reason_adhoc_rcsa_audit (
            reason_audit_id,
            reason_id,
            reason,
            status,
            created_by,
            created_at,
            updated_by,
            updated_at,
            comments,
            action
        ) VALUES (
            nextval('drm_sit.seq_reason_adhoc_rcsa_audit_id'),
            OLD.reason_id,
            OLD.reason,
            OLD.status,
            OLD.created_by,
            OLD.created_at,
            OLD.updated_by,
            OLD.updated_at,
            OLD.comments,
            'delete'
        );
        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- 5. Create Trigger
CREATE TRIGGER trg_reason_adhoc_rcsa_audit
AFTER INSERT OR UPDATE OR DELETE
ON drm_sit.rcsa_dropdown_reason_adhoc_rcsa
FOR EACH ROW
EXECUTE FUNCTION drm_sit.trg_reason_adhoc_rcsa_audit_func();



---

----


-- 1. Create sequence for main table primary key
CREATE SEQUENCE IF NOT EXISTS drm_sit.seq_user_role_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- 2. Create sequence for audit table primary key
CREATE SEQUENCE IF NOT EXISTS drm_sit.seq_user_role_audit_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- 3. Drop existing trigger and function if needed
DROP TRIGGER IF EXISTS trg_user_roles_audit ON drm_sit.rcsa_user_roles;
DROP FUNCTION IF EXISTS drm_sit.trg_user_roles_audit_func();

-- 4. Create trigger function
CREATE OR REPLACE FUNCTION drm_sit.trg_user_roles_audit_func()
RETURNS TRIGGER AS $$
BEGIN
    -- INSERT
    IF TG_OP = 'INSERT' THEN
        INSERT INTO drm_sit.rcsa_user_roles_audit (
            user_role_audit_id,
            user_role_id,
            user_role_name,
            description,
            status,
            action,
            created_by,
            updated_by,
            created_at,
            updated_at
        ) VALUES (
            nextval('drm_sit.seq_user_role_audit_id'),
            NEW.user_role_id,
            NEW.user_role_name,
            NEW.description,
            NEW.status,
            'add',
            NEW.created_by,
            NEW.updated_by,
            NEW.created_at,
            NEW.updated_at
        );
        RETURN NEW;

    -- UPDATE
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO drm_sit.rcsa_user_roles_audit (
            user_role_audit_id,
            user_role_id,
            user_role_name,
            description,
            status,
            action,
            created_by,
            updated_by,
            created_at,
            updated_at
        ) VALUES (
            nextval('drm_sit.seq_user_role_audit_id'),
            NEW.user_role_id,
            NEW.user_role_name,
            NEW.description,
            NEW.status,
            CASE
                WHEN OLD.status IS DISTINCT FROM NEW.status THEN
                    CASE
                        WHEN NEW.status = 'ACTIVE' THEN 'activate'
                        WHEN NEW.status = 'INACTIVE' THEN 'deactivate'
                        ELSE 'edit'
                    END
                ELSE 'edit'
            END,
            NEW.created_by,
            NEW.updated_by,
            NEW.created_at,
            NEW.updated_at
        );
        RETURN NEW;

    -- DELETE
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO drm_sit.rcsa_user_roles_audit (
            user_role_audit_id,
            user_role_id,
            user_role_name,
            description,
            status,
            action,
            created_by,
            updated_by,
            created_at,
            updated_at
        ) VALUES (
            nextval('drm_sit.seq_user_role_audit_id'),
            OLD.user_role_id,
            OLD.user_role_name,
            OLD.description,
            OLD.status,
            'delete',
            OLD.created_by,
            OLD.updated_by,
            OLD.created_at,
            OLD.updated_at
        );
        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- 5. Create the trigger on the main table
CREATE TRIGGER trg_user_roles_audit
AFTER INSERT OR UPDATE OR DELETE
ON drm_sit.rcsa_user_roles
FOR EACH ROW
EXECUTE FUNCTION drm_sit.trg_user_roles_audit_func();