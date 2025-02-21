import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.List;

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
    public Response exportToExcel(@QueryParam("columns") List<String> selectedColumns) {
        try {
            // Validate input
            if (selectedColumns != null) {
                for (String column : selectedColumns) {
                    if (column == null || column.trim().isEmpty()) {
                        throw new IllegalArgumentException("Column names cannot be null or empty");
                    }
                }
            }

            // Fetch data and determine columns
            List<RiskDetails> data = riskExportService.fetchRiskDetails();
            List<String> columns = riskExportService.getAvailableColumns(selectedColumns, columnOrder);

            if (data.isEmpty()) {
                LOG.warn("No data found for export");
                return Response.noContent().build();
            }

            // Generate Excel and return
            byte[] excelBytes = ExcelExporter.exportToExcel(data, columns);
            return Response
                    .ok(excelBytes)
                    .header("Content-Disposition", "attachment; filename=\"risk_details_export.xlsx\"")
                    .build();

        } catch (IllegalArgumentException e) {
            LOG.error("Invalid input parameters: " + e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid column names: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            LOG.error("Error generating Excel export: " + e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to generate Excel export: " + e.getMessage())
                    .build();
        }
    }
}