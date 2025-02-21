import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class RiskExportService {
    private static final Logger LOG = Logger.getLogger(RiskExportService.class);

    @Inject
    RiskDetailsRepository riskDetailsRepo;

    public List<RiskDetails> fetchRiskDetails(String raId) {
        try {
            if (raId == null || raId.trim().isEmpty()) {
                throw new IllegalArgumentException("raId cannot be null or empty");
            }
            return riskDetailsRepo.findByRaId(raId);
        } catch (Exception e) {
            LOG.error("Failed to fetch risk details for raId: " + raId, e);
            throw new RuntimeException("Database error occurred while fetching risk details for raId: " + raId, e);
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