public class AlertActionMapper {

    // DTO → Entity (used when saving a new action)
    public static AlertAction toEntity(AlertActionInputDto dto) {
        AlertAction entity = new AlertAction();
        entity.setAlertId(dto.alertId);
        entity.setActionStage(dto.actionStage);
        entity.setActionTaken(dto.actionTaken);
        entity.setActionBy(dto.actionBy);
        entity.setActionRole(dto.actionRole);
        entity.setActionNotes(dto.actionNotes);
        entity.setAssignNotes(dto.assignNotes);
        entity.setFindings(dto.findings);
        entity.setRiskManagementAction(dto.riskManagementAction);
        entity.setActionDate(LocalDateTime.now()); // Set current timestamp
        return entity;
    }

    // Entity → DTO (used for returning saved action)
public static AlertActionDetailsDto toDetailsDto(
            AlertData alert,
            List<AlertAction> actions,
            List<AlertRatingHistory> ratings,
            List<AlertStageDecision> stageDecisions) {

        var dto = new AlertActionDetailsDto();
        setCoreAlertFields(dto, alert);

        dto.actions = actions.stream()
                .map(AlertActionMapper::toDto)
                .toList();

        dto.ratings = ratings.stream()
                .map(AlertActionMapper::toRatingDto)
                .toList();

        dto.stageDecisions = stageDecisions.stream()
                .map(AlertActionMapper::toStageDecisionDto)
                .toList();

        return dto;
    }

    

private static void setCoreAlertFields(AlertActionDetailsDto dto, AlertData alert) {
        dto.alertId = alert.getAlertId();
        dto.alertDate = alert.getAlertDate();
        dto.typeOfAlert = alert.getTypeOfAlert();
        dto.ruleName = alert.getRuleName();
        dto.ruleDescription = alert.getRuleDescription();
        dto.criteria = alert.getCriteria();
        dto.riskType = alert.getRiskType();
        dto.rating = alert.getRating();
        dto.factor = alert.getFactor();
        dto.country = alert.getCountry();
        dto.assessmentUnit = alert.getAssessmentUnit();
        dto.segment = alert.getSegment();
        dto.subsegment = alert.getSubsegment();
        dto.alertStatus = alert.getAlertStatus();
        dto.dueDate = alert.getDueDate();
        dto.latestReminderDate = alert.getLatestReminderDate();
        dto.escalationDate = alert.getEscalationDate();
        dto.alertAge = alert.getAlertAge();
    }

    private static AlertActionDto toDto(AlertAction entity) {
        var dto = new AlertActionDto();
        dto.actionId = entity.getActionId();
        dto.actionStage = entity.getActionStage();
        dto.actionTaken = entity.getActionTaken();
        dto.actionBy = entity.getActionBy();
        dto.actionRole = entity.getActionRole();
        dto.actionDate = entity.getActionDate();
        dto.actionNotes = entity.getActionNotes();
        dto.assignNotes = entity.getAssignNotes();
        dto.findings = entity.getFindings();
        dto.riskManagementAction = entity.getRiskManagementAction();
        return dto;
    }

    private static AlertRatingHistoryDto toRatingDto(AlertRatingHistory entity) {
        var dto = new AlertRatingHistoryDto();
        dto.ratingId = entity.getRatingId();
        dto.ratingType = entity.getRatingType();
        dto.ratingValue = entity.getRatingValue();
        dto.ratingDate = entity.getRatingDate();
        return dto;
    }

    private static AlertStageDecisionDto toStageDecisionDto(AlertStageDecision entity) {
        var dto = new AlertStageDecisionDto();
        dto.decisionId = entity.getDecisionId();
        dto.stage = entity.getStage();
        dto.role = entity.getRole();
        dto.decision = entity.getDecision();
        dto.actionDate = entity.getActionDate();
        dto.findings = entity.getFindings();
        dto.riskManagementAction = entity.getRiskManagementAction();
        return dto;
}

   /**
     * Maps AlertDashboardView to AlertDashboardDto
     */
    public static AlertDashboardDto toDashboardDto(AlertDashboardView view) {
        var dto = new AlertDashboardDto();
        dto.alertId = view.alertId;
        dto.alertDate = view.alertDate;
        dto.typeOfAlert = view.typeOfAlert;
        dto.ruleName = view.ruleName;
        dto.ruleDescription = view.ruleDescription;
        dto.riskType = view.riskType;
        dto.rating = view.rating;
        dto.factor = view.factor;
        dto.country = view.country;
        dto.assessmentUnit = view.assessmentUnit;
        dto.segment = view.segment;
        dto.subsegment = view.subsegment;
        dto.assignedRole = view.assignedRole;
        dto.assignedTo = view.assignedTo;
        dto.alertStatus = view.alertStatus;
        dto.lastActionTaken = view.lastActionTaken;
        dto.lastActionDate = view.lastActionDate;
        dto.dueDate = view.dueDate;
        dto.latestReminderDate = view.latestReminderDate;
        dto.escalationDate = view.escalationDate;
        dto.alertAge = view.alertAge;
        return dto;
    }

    
}