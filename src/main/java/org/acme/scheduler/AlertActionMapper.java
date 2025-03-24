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
    public static AlertActionDto toDto(AlertAction entity) {
        AlertActionDto dto = new AlertActionDto();
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
}