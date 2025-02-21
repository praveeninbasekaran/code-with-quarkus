import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.jboss.logging.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class RiskExportService {
    private static final Logger LOG = Logger.getLogger(RiskExportService.class);

    public List<RiskDetails> fetchRiskDetails() {
        try {
            return RiskDetails.listAll();
        } catch (Exception e) {
            LOG.error("Failed to fetch risk details", e);
            throw new RuntimeException("Database error occurred while fetching risk details", e);
        }
    }

    public List<String> getAvailableColumns(List<String> selectedColumns, String defaultColumnOrder) {
        List<String> defaultColumns = Arrays.asList(defaultColumnOrder.split(","));
        if (selectedColumns == null || selectedColumns.isEmpty()) {
            return defaultColumns;
        }
        return selectedColumns.stream()
                .filter(column -> defaultColumns.contains(column))
                .distinct()
                .collect(Collectors.toList());
    }
}
