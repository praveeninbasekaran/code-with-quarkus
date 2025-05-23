import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    public List<String> getAvailableColumnsForStages(Map<String, List<String>> stageColumnsMap, String defaultColumnOrder) {
        List<String> defaultColumns = Arrays.asList(defaultColumnOrder.split(","));
        
        if (stageColumnsMap == null || stageColumnsMap.isEmpty()) {
            return defaultColumns;
        }

        // Collect all unique columns across all stages
        Set<String> allColumns = new HashSet<>();
        for (List<String> columns : stageColumnsMap.values()) {
            allColumns.addAll(columns.stream()
                    .filter(col -> defaultColumns.contains(col))
                    .collect(Collectors.toList()));
        }

        return allColumns.stream()
                .distinct()
                .filter(defaultColumns::contains)
                .collect(Collectors.toList());
    }
}