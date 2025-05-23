CREATE TABLE role_dropdown_mapping (
    id SERIAL PRIMARY KEY,
    role VARCHAR NOT NULL,
    risk_level1 VARCHAR,
    risk_level2 VARCHAR,
    risk_level3 VARCHAR,
    business_function_l1 VARCHAR,
    business_function_l2 VARCHAR,
    business_function_l3 VARCHAR,
    country VARCHAR,
    legality VARCHAR
);

-- Role: Risk level 1 framework owner → Only Risk Level 1 is applicable
INSERT INTO role_dropdown_mapping (role, risk_level1)
VALUES
('Risk level 1 framework owner', 'Financial Crime'),
('Risk level 1 framework owner', 'Compliance'),
('Risk level 1 framework owner', 'Operations');

-- Role: Risk level 2 framework owner → Risk Level 1 and 2
INSERT INTO role_dropdown_mapping (role, risk_level1, risk_level2)
VALUES
('Risk level 2 framework owner', 'Financial Crime', 'Sanctions'),
('Risk level 2 framework owner', 'Financial Crime', 'Transaction failure');

-- Role: Business function L1 (ILOD) → Business Function L1, L2, Country
INSERT INTO role_dropdown_mapping (role, business_function_l1, business_function_l2, country)
VALUES
('Business function L1 (ILOD)', 'Investment banking', 'Financial Market', 'Srilanka'),
('Business function L1 (ILOD)', 'Investment banking', 'Financial Market', 'India');

-- Role: Country Risk owner → Business Function L1, L2, Country
INSERT INTO role_dropdown_mapping (role, business_function_l1, business_function_l2, country)
VALUES
('Country Risk owner', 'Investment banking', 'Financial Market', 'Srilanka'),
('Country Risk owner', 'Investment banking', 'Financial Market', 'India');


-- 1. Create or Replace the Trigger Function
CREATE OR REPLACE FUNCTION drm_sit.trg_rating_overlay_category_audit_func()
RETURNS TRIGGER AS $$
BEGIN
    -- INSERT action: when a new row is added
    IF TG_OP = 'INSERT' THEN
        INSERT INTO drm_sit.rcsa_dropdown_rating_overlay_category_audit (
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

    -- UPDATE action: edit or status change
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO drm_sit.rcsa_dropdown_rating_overlay_category_audit (
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

    -- DELETE action
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO drm_sit.rcsa_dropdown_rating_overlay_category_audit (
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

-- 2. Drop Existing Trigger if it exists (Optional Safety)
DROP TRIGGER IF EXISTS trg_rating_overlay_category_audit
ON drm_sit.rcsa_dropdown_rating_overlay_category;

-- 3. Create the Trigger on Main Table
CREATE TRIGGER trg_rating_overlay_category_audit
AFTER INSERT OR UPDATE OR DELETE
ON drm_sit.rcsa_dropdown_rating_overlay_category
FOR EACH ROW
EXECUTE FUNCTION drm_sit.trg_rating_overlay_category_audit_func();

---



-- 1. Create or Replace Trigger Function
CREATE OR REPLACE FUNCTION drm_sit.trg_reason_for_adhoc_rcsa_audit_func()
RETURNS TRIGGER AS $$
BEGIN
    -- INSERT action
    IF TG_OP = 'INSERT' THEN
        INSERT INTO drm_sit.rcsa_dropdown_reason_for_adhoc_rcsa_audit (
            reason_for_adhoc_rcsa_id,
            reason_for_adhoc_rcsa,
            status,
            created_by,
            created_at,
            updated_by,
            updated_at,
            comments,
            action
        ) VALUES (
            NEW.reason_for_adhoc_rcsa_id,
            NEW.reason_for_adhoc_rcsa,
            NEW.status,
            NEW.created_by,
            NEW.created_at,
            NEW.updated_by,
            NEW.updated_at,
            NEW.comments,
            'add'
        );
        RETURN NEW;

    -- UPDATE action
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO drm_sit.rcsa_dropdown_reason_for_adhoc_rcsa_audit (
            reason_for_adhoc_rcsa_id,
            reason_for_adhoc_rcsa,
            status,
            created_by,
            created_at,
            updated_by,
            updated_at,
            comments,
            action
        ) VALUES (
            NEW.reason_for_adhoc_rcsa_id,
            NEW.reason_for_adhoc_rcsa,
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

    -- DELETE action
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO drm_sit.rcsa_dropdown_reason_for_adhoc_rcsa_audit (
            reason_for_adhoc_rcsa_id,
            reason_for_adhoc_rcsa,
            status,
            created_by,
            created_at,
            updated_by,
            updated_at,
            comments,
            action
        ) VALUES (
            OLD.reason_for_adhoc_rcsa_id,
            OLD.reason_for_adhoc_rcsa,
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

-- 2. Drop existing trigger if it exists
DROP TRIGGER IF EXISTS trg_reason_for_adhoc_rcsa_audit
ON drm_sit.rcsa_dropdown_reason_for_adhoc_rcsa;

-- 3. Create the new trigger
CREATE TRIGGER trg_reason_for_adhoc_rcsa_audit
AFTER INSERT OR UPDATE OR DELETE
ON drm_sit.rcsa_dropdown_reason_for_adhoc_rcsa
FOR EACH ROW
EXECUTE FUNCTION drm_sit.trg_reason_for_adhoc_rcsa_audit_func();
