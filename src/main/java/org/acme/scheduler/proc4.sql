CREATE OR REPLACE PROCEDURE strap_sit.copy_risk_assessment (
    v_new_ra_id character varying,
    v_cpy_ra_id character varying,
    v_unit_name character varying
)
LANGUAGE 'plpgsql'
AS $BODY$
DECLARE
    v_stage character varying;
    stages text[] := ARRAY[
        'inherent_risk_assessment',
        'mitigating_1lod',
        'additional_mitigating_2LoD_controls',
        'mitigating_surveillance_controls',
        'residual_risk_assessment'
    ];
    -- Array of JSONB keys to nullify
    keys_to_nullify text[] := ARRAY[
        'modifiedInherentRiskRating',
        'modifiedInherentRiskRatingRationale'
    ];
    key_to_nullify text;
    modified_value jsonb;
BEGIN
    -- Loop through each stage
    FOR i IN 1..array_length(stages, 1) LOOP
        v_stage := stages[i];

        -- Process each key that needs to be nullified
        FOR key_to_nullify IN SELECT unnest(keys_to_nullify) LOOP
            INSERT INTO strap_sit.t_risk_assessement_details_updated_bkp (
                ra_id, risk_behaviour, unit_name, assessment_stage, meta_version_number, value,
                risk_assessment_details_id, stage, validated
            )
            WITH library_detail_2 AS (
                SELECT
                    root_elem->>'riskBehaviourName' AS riskBehaviourName,
                    root_elem
                FROM strap_sit.library_data data,
                LATERAL jsonb_array_elements(data.library_content) AS root_elem
                WHERE library_id = 2
                AND root_elem->>'status' = 'Active'
            ), risk AS (
                SELECT *
                FROM strap_sit.t_risk_assessement_details_updated_bkp risk
                WHERE risk.ra_id = v_cpy_ra_id
                AND risk.unit_name = v_unit_name
                AND risk.stage = v_stage
            )
            SELECT
                v_new_ra_id,
                risk.risk_behaviour AS risk_behaviour,
                risk.unit_name AS unit_name,
                risk.assessment_stage AS assessment_stage,
                risk.meta_version_number AS meta_version_number,
                -- Modify the JSONB value to nullify the specified key if it exists
                CASE 
                    WHEN risk.value ? key_to_nullify 
                    THEN jsonb_set(risk.value, ARRAY[key_to_nullify], 'null'::jsonb)
                    ELSE risk.value
                END AS value,
                risk.risk_assessment_details_id AS risk_assessment_details_id,
                risk.stage AS stage,
                risk.validated AS validated
            FROM risk
            JOIN library_detail_2 lib
            ON (risk.risk_behaviour = lib.riskBehaviourName);
        END LOOP;
    END LOOP;
EXCEPTION
    WHEN OTHERS THEN
        RAISE EXCEPTION 'Error in copy_risk_assessment: %', SQLERRM;
END;
$BODY$;

ALTER PROCEDURE strap_sit.copy_risk_assessment
(character varying, character varying, character varying)
OWNER TO svc_riskview;