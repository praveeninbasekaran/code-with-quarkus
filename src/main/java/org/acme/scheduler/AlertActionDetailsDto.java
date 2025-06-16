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



private String sanitizeJson(String jsonContent) {
    try {
        // Try parsing directly
        objectMapper.readTree(jsonContent);
        return jsonContent; // Already valid
    } catch (JsonProcessingException e) {
        logger.error("Invalid JSON format: {}", e.getMessage());

        // Attempt to fix common issues
        String sanitizedJson = jsonContent
            // Add quotes around unquoted keys (start of line or after {, commas)
            .replaceAll("(?<=[{,\\s])([a-zA-Z0-9_]+)(?=\\s*:)", "\"$1\"")
            // Remove trailing commas in objects and arrays
            .replaceAll(",\\s*([}\\]])", "$1");

        try {
            // Re-validate after fix
            objectMapper.readTree(sanitizedJson);
            return sanitizedJson;
        } catch (JsonProcessingException ex) {
            logger.error("Failed to sanitize JSON: {}", ex.getMessage());
            throw new IllegalArgumentException("Invalid JSON format after sanitization. Original error: " + e.getMessage(), ex);
        }
    }
}