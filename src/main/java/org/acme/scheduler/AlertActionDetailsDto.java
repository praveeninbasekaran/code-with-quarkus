public class AlertActionDetailsDto {
    public Long alertId;
    public LocalDateTime alertDate;
    public String typeOfAlert;
    public String ruleName;
    public String ruleDescription;
    public String criteria;
    public String riskType;
    public String rating;
    public String factor;
    public String country;
    public String assessmentUnit;
    public String segment;
    public String subsegment;
    public String alertStatus;
    public LocalDateTime dueDate;
    public LocalDateTime latestReminderDate;
    public LocalDateTime escalationDate;
    public Integer alertAge;

    // Nested details
    public List<AlertActionDto> actions;
    public List<AlertRatingHistoryDto> ratings;
    public List<AlertStageDecisionDto> stageDecisions;
}