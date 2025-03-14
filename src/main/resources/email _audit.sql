CREATE OR REPLACE FUNCTION email_template_audit_trigger_fn()
RETURNS TRIGGER AS $$
BEGIN
    BEGIN
        -- If a new record is inserted
        IF TG_OP = 'INSERT' THEN
            INSERT INTO drm_sit.email_template_audit (
                id, name, to_address, cc_address, subject, body, status, created_by, created_at, updated_by, updated_at
            )
            VALUES (
                COALESCE(NEW.id, 0),
                COALESCE(NEW.name, ''),
                COALESCE(NEW.to_address, ''),
                COALESCE(NEW.cc_address, ''),
                COALESCE(NEW.subject, ''),
                COALESCE(NEW.body, ''),
                COALESCE(NEW.status, 'Unknown'),
                COALESCE(NEW.created_by, 'system'),
                COALESCE(NEW.created_at, now()),
                COALESCE(NEW.updated_by, 'system'),
                COALESCE(NEW.updated_at, now())
            );

        -- If an existing record is updated, store the new values in audit table
        ELSIF TG_OP = 'UPDATE' THEN
            INSERT INTO drm_sit.email_template_audit (
                id, name, to_address, cc_address, subject, body, status, created_by, created_at, updated_by, updated_at
            )
            VALUES (
                COALESCE(NEW.id, 0),
                COALESCE(NEW.name, ''),
                COALESCE(NEW.to_address, ''),
                COALESCE(NEW.cc_address, ''),
                COALESCE(NEW.subject, ''),
                COALESCE(NEW.body, ''),
                COALESCE(NEW.status, 'Unknown'),
                COALESCE(NEW.created_by, 'system'),
                COALESCE(NEW.created_at, now()),
                COALESCE(NEW.updated_by, 'system'),
                COALESCE(NEW.updated_at, now())
            );

        -- If a record is deleted, store the old values in audit table
        ELSIF TG_OP = 'DELETE' THEN
            INSERT INTO drm_sit.email_template_audit (
                id, name, to_address, cc_address, subject, body, status, created_by, created_at, updated_by, updated_at
            )
            VALUES (
                COALESCE(OLD.id, 0),
                COALESCE(OLD.name, ''),
                COALESCE(OLD.to_address, ''),
                COALESCE(OLD.cc_address, ''),
                COALESCE(OLD.subject, ''),
                COALESCE(OLD.body, ''),
                COALESCE(OLD.status, 'Unknown'),
                COALESCE(OLD.created_by, 'system'),
                COALESCE(OLD.created_at, now()),
                COALESCE(OLD.updated_by, 'system'),
                COALESCE(OLD.updated_at, now())
            );
        END IF;

        -- Return NULL since this is an AFTER trigger
        RETURN NULL;

    -- Exception Handling: Logs the error instead of failing transaction
    EXCEPTION 
        WHEN OTHERS THEN 
            RAISE WARNING 'Error in email_template_audit_trigger_fn: %', SQLERRM;
            RETURN NULL;
    END;
END;
$$ LANGUAGE plpgsql;