@ApplicationScoped
public class AlertDashboardViewRepository implements PanacheRepositoryBase<AlertDashboardView, Long> {

    public List<AlertDashboardView> findAllDashboardAlerts() {
        return listAll();
    }

    public Optional<AlertDashboardView> findByAlertId(Long alertId) {
        return find("alertId", alertId).firstResultOptional();
    }
}