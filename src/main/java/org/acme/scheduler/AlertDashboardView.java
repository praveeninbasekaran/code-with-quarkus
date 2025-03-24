@Entity
@Immutable
@Table(name = "alert_t_alert_dashboard_all", schema = "drm_sit")
public class AlertDashboardView {

    @Id
    @Column(name = "alert_id")
    public Long alertId;

    @Column(name = "alert_date")
    public LocalDateTime alertDate;

    public String typeOfAlert;
    public String ruleName;
    public String ruleDescription;

    public String riskType;
    public String rating;
    public String factor;
    public String country;
    public String assessmentUnit;
    public String segment;
    public String subsegment;

    public String assignedRole;
    public String assignedTo;
    public String alertStatus;
    public String lastActionTaken;
    public String lastActionDate;

    public LocalDateTime dueDate;
    public LocalDateTime latestReminderDate;
    public LocalDateTime escalationDate;
    public Integer alertAge;
}