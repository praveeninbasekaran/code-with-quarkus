import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/export")
public class ExportResource {
    private static final Logger LOG = Logger.getLogger(ExportResource.class);

    @Inject
    RiskExportService riskExportService;

    @ConfigProperty(name = "export.columns.order")
    String columnOrder;

    @GET
    @Path("/excel")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response exportToExcel(@QueryParam("raId") String raId, @QueryParam("stagesAndColumns") String stagesAndColumns) {
        try {
            // Validate raId
            if (raId == null || raId.trim().isEmpty()) {
                throw new IllegalArgumentException("raId cannot be null or empty");
            }

            // Parse stagesAndColumns query param into a Map<stage, List<columns>>
            Map<String, List<String>> stageColumnsMap = parseStagesAndColumns(stagesAndColumns);

            // Fetch data and determine columns based on stage
            List<RiskDetails> data = riskExportService.fetchRiskDetails(raId);
            List<String> columns = riskExportService.getAvailableColumnsForStages(stageColumnsMap, columnOrder);

            if (data.isEmpty()) {
                LOG.warn("No data found for raId: " + raId);
                return Response.noContent().build();
            }

            // Generate Excel and return
            byte[] excelBytes = ExcelExporter.exportToExcel(data, columns);
            return Response
                    .ok(excelBytes)
                    .header("Content-Disposition", "attachment; filename=\"risk_details_export_" + raId + ".xlsx\"")
                    .build();

        } catch (IllegalArgumentException e) {
            LOG.error("Invalid input parameters: " + e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid parameters: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            LOG.error("Error generating Excel export for raId: " + raId + ": " + e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to generate Excel export: " + e.getMessage())
                    .build();
        }
    }

    private Map<String, List<String>> parseStagesAndColumns(String stagesAndColumns) {
        if (stagesAndColumns == null || stagesAndColumns.trim().isEmpty()) {
            return new HashMap<>();
        }

        Map<String, List<String>> stageColumnsMap = new HashMap<>();
        String[] stageEntries = stagesAndColumns.split("&");
        for (String entry : stageEntries) {
            String[] parts = entry.split("=");
            if (parts.length == 2) {
                String stage = parts[0];
                List<String> columns = Arrays.asList(parts[1].split(","))
                        .stream()
                        .filter(col -> col != null && !col.trim().isEmpty())
                        .map(String::trim)
                        .collect(Collectors.toList());
                stageColumnsMap.put(stage, columns);
            }
        }
        return stageColumnsMap;
    }
}