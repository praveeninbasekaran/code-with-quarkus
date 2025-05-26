@Path("/export")
@Produces(MediaType.APPLICATION_OCTET_STREAM)
@Consumes(MediaType.APPLICATION_JSON)
public class ExcelExportController {

    @Inject
    ExcelExportService excelExportService;

    @POST
    @Path("/excel")
    public Response exportExcel(ExportRequest request) throws Exception {
        File excelFile = excelExportService.exportToExcel(request);

        return Response.ok((StreamingOutput) output -> {
            try (FileInputStream in = new FileInputStream(excelFile)) {
                byte[] buffer = new byte[4096];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
            }
        })
        .header("Content-Disposition", "attachment; filename=\"" + excelFile.getName() + "\"")
        .build();
    }
}

@ApplicationScoped
public class ExcelExportService {

    @Inject
    DataSource dataSource;

    public File exportToExcel(ExportRequest request) throws Exception {
        File file = File.createTempFile(request.tableName + "_export_", ".xlsx");

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100); // flush rows every 100
             FileOutputStream out = new FileOutputStream(file);
             Connection conn = dataSource.getConnection()) {

            conn.setAutoCommit(false);
            Sheet sheet = workbook.createSheet("Export");

            String sql = buildQuery(request);
            try (PreparedStatement ps = conn.prepareStatement(sql,
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY)) {

                ps.setFetchSize(1000); // enable streaming for large data

                try (ResultSet rs = ps.executeQuery()) {
                    ResultSetMetaData meta = rs.getMetaData();
                    int columnCount = meta.getColumnCount();

                    // Header row
                    Row header = sheet.createRow(0);
                    for (int i = 1; i <= columnCount; i++) {
                        header.createCell(i - 1).setCellValue(meta.getColumnLabel(i));
                    }

                    // Data rows
                    int rowNum = 1;
                    while (rs.next()) {
                        Row row = sheet.createRow(rowNum++);
                        for (int i = 1; i <= columnCount; i++) {
                            Object value = rs.getObject(i);
                            row.createCell(i - 1).setCellValue(value != null ? value.toString() : "");
                        }
                    }
                }
            }

            workbook.write(out);
        }

        return file;
    }

    private String buildQuery(ExportRequest req) {
        StringBuilder query = new StringBuilder("SELECT ");

        if (req.selectedColumns != null && !req.selectedColumns.isEmpty()) {
            query.append(String.join(", ", req.selectedColumns));
        } else {
            query.append("*");
        }

        query.append(" FROM ").append(req.tableName);

        if (req.filters != null && !req.filters.isEmpty()) {
            query.append(" WHERE ");
            List<String> conditions = req.filters.entrySet().stream()
                .map(entry -> entry.getKey() + " ILIKE '%" + entry.getValue() + "%'")
                .collect(Collectors.toList());
            query.append(String.join(" AND ", conditions));
        }

        if (req.sortBy != null && !req.sortBy.isEmpty()) {
            query.append(" ORDER BY ")
                 .append(req.sortBy)
                 .append(" ")
                 .append(req.sortDirection != null ? req.sortDirection : "ASC");
        }

        return query.toString();
    }
}

----