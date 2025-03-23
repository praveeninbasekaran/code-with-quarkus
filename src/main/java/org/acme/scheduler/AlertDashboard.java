@Entity
@Table(name = "alert_t_alert_data")
public class AlertData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alertId;

    private Long ruleId;

    private LocalDateTime alertDate;

    private String typeOfAlert;
    private String ruleName;
    private String ruleDescription;
    private String criteria;

    private String riskType;
    private String rating;
    private String factor;
    private String country;
    private String assessmentUnit;
    private String segment;
    private String subsegment;

    private String alertStatus;
    private LocalDateTime dueDate;
    private LocalDateTime latestReminderDate;
    private LocalDateTime escalationDate;

    private Integer alertAge;

    // Getters and setters
}


@Entity
@Table(name = "alert_t_actions")
public class AlertAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long actionId;

    private Long alertId;
    private Integer actionStage;
    private String actionTaken;
    private Integer actionBy;
    private String actionRole;

    private LocalDateTime actionDate;

    private String actionNotes;
    private String assignNotes;

    @Column(length = 5000)
    private String findings;

    @Column(length = 5000)
    private String riskManagementAction;

    // Getters and setters
}

@Entity
@Table(name = "alert_t_ratings_history")
public class AlertRatingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ratingId;

    private Long alertId;

    private String ratingType; // e.g., Current / Previous
    private String ratingValue;

    private LocalDateTime ratingDate;

    // Getters and setters
}

@Entity
@Table(name = "alert_t_stage_decisions")
public class AlertStageDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long decisionId;

    private Long alertId;
    private Integer stage;

    private String role;
    private String decision;

    private LocalDateTime actionDate;

    @Column(length = 5000)
    private String findings;

    @Column(length = 5000)
    private String riskManagementAction;

    // Getters and setters
}

@Entity
@Table(name = "alert_audit_history")
public class AlertAuditHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    private Long alertId;
    private Integer actionStage;

    private String userRole;
    private Integer userId;

    private String actionTaken;
    private String assignedToRole;
    private Integer assignedToUser;

    private LocalDateTime actionDate;

    @Column(length = 1000)
    private String assignNotes;

    @Column(length = 5000)
    private String comments;

    @Column(length = 5000)
    private String ruleManagementAction;

    // Getters and setters
}






