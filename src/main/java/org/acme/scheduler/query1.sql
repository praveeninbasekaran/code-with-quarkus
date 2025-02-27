SELECT 
    j ->> 'riskBehaviourName' AS risk_behaviour_name,
    j ->> 'riskBehaviourDescription' AS risk_behaviour_description,
    j ->> 'causeTypeDescription' AS cause_type_description,
    j ->> 'regulation' AS regulation,
    j ->> 'riskTypeName' AS risk_type_name,
    j ->> 'riskSubTypeName' AS risk_sub_type_name,
    j ->> 'riskAssessmentTemplateName' AS risk_assessment_template_name,
    j ->> 'status' AS status,
    j ->> 'rowIndex' AS row_index,
    CASE 
        WHEN rad.risk_behaviour_name IS NOT NULL THEN true 
        ELSE false 
    END AS isPresent_in_t_risk_assessment_details_updated
FROM strap_uat.library_data ld
CROSS JOIN LATERAL jsonb_array_elements(ld.library_content) AS t(j)
LEFT JOIN t_risk_assessment_details_updated rad 
    ON j ->> 'riskBehaviourName' = rad.risk_behaviour_name
    AND rad.ra_id = 'RA123'
    AND rad.unit_name = 'UnitA'
    AND rad.stage = 'Stage1'
WHERE ld.library_id = 2 
    AND j ->> 'status' = 'Active';

------