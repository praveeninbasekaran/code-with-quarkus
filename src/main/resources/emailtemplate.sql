-- Step 1: Create Audit Function
CREATE OR REPLACE FUNCTION sb_55313_116_drm.fn_email_template_audit_trigger()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        INSERT INTO sb_55313_116_drm.email_template_audit (
            audit_id, id, name, to_address, cc_address, subject, body, status,
            created_by, created_at, updated_by, updated_at, type
        )
        VALUES (
            DEFAULT, NEW.id, NEW.name, NEW.to_address, NEW.cc_address, NEW.subject, NEW.body, NEW.status,
            NEW.created_by, NEW.created_at, NEW.updated_by, NEW.updated_at, NEW.type
        );
        RETURN NEW;

    ELSIF (TG_OP = 'UPDATE') THEN
        INSERT INTO sb_55313_116_drm.email_template_audit (
            audit_id, id, name, to_address, cc_address, subject, body, status,
            created_by, created_at, updated_by, updated_at, type
        )
        VALUES (
            DEFAULT, NEW.id, NEW.name, NEW.to_address, NEW.cc_address, NEW.subject, NEW.body, NEW.status,
            NEW.created_by, NEW.created_at, NEW.updated_by, NEW.updated_at, NEW.type
        );
        RETURN NEW;

    ELSIF (TG_OP = 'DELETE') THEN
        INSERT INTO sb_55313_116_drm.email_template_audit (
            audit_id, id, name, to_address, cc_address, subject, body, status,
            created_by, created_at, updated_by, updated_at, type
        )
        VALUES (
            DEFAULT, OLD.id, OLD.name, OLD.to_address, OLD.cc_address, OLD.subject, OLD.body, OLD.status,
            OLD.created_by, OLD.created_at, OLD.updated_by, OLD.updated_at, OLD.type
        );
        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Step 2: Create Trigger
DROP TRIGGER IF EXISTS trg_email_template_audit ON sb_55313_116_drm.email_template;

CREATE TRIGGER trg_email_template_audit
AFTER INSERT OR UPDATE OR DELETE ON sb_55313_116_drm.email_template
FOR EACH ROW
EXECUTE FUNCTION sb_55313_116_drm.fn_email_template_audit_trigger();