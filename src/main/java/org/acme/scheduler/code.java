void processRiskAssessments(Request request) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    List<RiskAssessmentDetail> riskAssessmentDetails = new ArrayList<>();

    for (String assessmentUnitName : request.getAssessmentUnitNames()) {
        for (RiskBehaviourView riskBehaviour : riskBehaviourAU) {
            
            // Collect data for batch insert instead of calling DB multiple times
            riskAssessmentDetails.add(createRiskAssessmentDetail(request, riskBehaviour, assessmentUnitName, 
                    "inherent_risk_assessment", objectMapper));

            riskAssessmentDetails.add(createRiskAssessmentDetail(request, riskBehaviour, assessmentUnitName, 
                    "mitigating_1lod", objectMapper));

            riskAssessmentDetails.add(createRiskAssessmentDetail(request, riskBehaviour, assessmentUnitName, 
                    "additional_mitigating_2LoD_controls", objectMapper));

            riskAssessmentDetails.add(createRiskAssessmentDetail(request, riskBehaviour, assessmentUnitName, 
                    "mitigating_surveillance_controls", objectMapper));

            riskAssessmentDetails.add(createRiskAssessmentDetail(request, riskBehaviour, assessmentUnitName, 
                    "residual_risk_assessment", objectMapper));
        }
    }
    
    // Batch insert the collected data
    postRiskAssessmentDetailsBulk(riskAssessmentDetails);
}

/**
 * Helper method to create RiskAssessmentDetail object.
 */
private RiskAssessmentDetail createRiskAssessmentDetail(Request request, RiskBehaviourView riskBehaviour, 
        String assessmentUnitName, String stage, ObjectMapper objectMapper) throws JsonProcessingException {

    Map<String, Object> additionalFields = new LinkedHashMap<>();
    switch (stage) {
        case "inherent_risk_assessment":
            setInherentRiskAdditionalFieldsMap(additionalFields);
            break;
        case "mitigating_1lod":
            setMitigating1lodAdditionalFields(additionalFields);
            break;
        case "additional_mitigating_2LoD_controls":
            setMitigating2lodAdditionalFields(additionalFields);
            break;
        case "mitigating_surveillance_controls":
            setSurveillanceControlAdditionalFields(additionalFields);
            break;
        case "residual_risk_assessment":
            setResidualRiskAssessmentAdditionalFields(additionalFields);
            break;
    }

    Map<String, Object> jsonStructure = createRiskBehaviourFields(
            riskBehaviour, assessmentUnitName, 
            RiskAssessmentUnitStatus.Inherent_Risk_Assessment, additionalFields
    );

    return new RiskAssessmentDetail(
            request.getRiskAssessmentId(), 
            riskBehaviour.getRiskBehaviourName(), 
            assessmentUnitName, 
            objectMapper.writeValueAsString(jsonStructure), 
            stage
    );
}

/**
 * Batch insert method.
 */
void postRiskAssessmentDetailsBulk(List<RiskAssessmentDetail> riskAssessmentDetails) {
    // Batch insert logic (e.g., Panache EntityManager.persist())
    riskAssessmentRepository.persist(riskAssessmentDetails);
}