package your.package.name;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javax.sql.DataSource;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class ExcelExportService {

    @Inject
    DataSource dataSource;

    // Functionality to table mapping
    private static final Map<String, String> FUNCTION_TABLE_MAP = Map.of(
        "userManagement", "users_table",
        "productList", "products_table",
        "transactionSummary", "transactions_table"
    );

    // Functionality-specific column header mappings
    private static final Map<String, Map<String, String>> FUNCTION_COLUMN_HEADERS = Map.of(
        "userManagement", Map.of(
            "user_id", "User ID",
            "username", "Username",
            "email", "Email Address",
            "status", "Status"
        ),
        "productList", Map.of(
            "product_id", "Product ID",
            "product_name", "Product Name",
            "price", "Price",
            "status", "Status"
        ),
        "transactionSummary", Map.of(
            "txn_id", "Transaction ID",
            "amount", "Amount",
            "txn_date", "Transaction Date",
            "status", "Status"
        )
        // Add more functionality-specific header mappings as needed
    );

    public File exportToExcel(ExportRequest request) throws Exception {
        String functionality = request.functionalityName;
        String tableName = resolveTableName(functionality);
        if (tableName == null) {
            throw new IllegalArgumentException("Invalid functionality name: " + functionality);
        }

        // Get column headers for this functionality
        Map<String, String> columnHeaders = FUNCTION_COLUMN_HEADERS.getOrDefault(functionality, Collections.emptyMap());

        File file = File.createTempFile(tableName + "_export_", ".xlsx");

        try (
            SXSSFWorkbook workbook = new SXSSFWorkbook(100);
            FileOutputStream out = new FileOutputStream(file);
            Connection conn = dataSource.getConnection()
        ) {
            conn.setAutoCommit(false);
            Sheet sheet = workbook.createSheet("Export");

            String sql = buildQuery(request, tableName);

            try (
                PreparedStatement ps = conn.prepareStatement(sql,
                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_READ_ONLY)
            ) {
                ps.setFetchSize(1000);

                try (ResultSet rs = ps.executeQuery()) {
                    ResultSetMetaData meta = rs.getMetaData();
                    int columnCount = meta.getColumnCount();

                    // Header style
                    CellStyle headerStyle = workbook.createCellStyle();
                    Font headerFont = workbook.createFont();
                    headerFont.setBold(true);
                    headerFont.setColor(IndexedColors.WHITE.getIndex());
                    headerStyle.setFont(headerFont);
                    headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
                    headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    headerStyle.setBorderTop(BorderStyle.THIN);
                    headerStyle.setBorderBottom(BorderStyle.THIN);
                    headerStyle.setBorderLeft(BorderStyle.THIN);
                    headerStyle.setBorderRight(BorderStyle.THIN);

                    // Data style
                    CellStyle dataStyle = workbook.createCellStyle();
                    dataStyle.setBorderTop(BorderStyle.THIN);
                    dataStyle.setBorderBottom(BorderStyle.THIN);
                    dataStyle.setBorderLeft(BorderStyle.THIN);
                    dataStyle.setBorderRight(BorderStyle.THIN);

                    // Header row
                    Row header = sheet.createRow(0);
                    for (int i = 1; i <= columnCount; i++) {
                        String columnLabel = meta.getColumnLabel(i);
                        String displayName = columnHeaders.getOrDefault(columnLabel, toTitleCase(columnLabel));
                        Cell cell = header.createCell(i - 1);
                        cell.setCellValue(displayName);
                        cell.setCellStyle(headerStyle);
                    }

                    // Data rows
                    int rowNum = 1;
                    while (rs.next()) {
                        Row row = sheet.createRow(rowNum++);
                        for (int i = 1; i <= columnCount; i++) {
                            Object value = rs.getObject(i);
                            Cell cell = row.createCell(i - 1);
                            cell.setCellValue(value != null ? value.toString() : "");
                            cell.setCellStyle(dataStyle);
                        }
                    }

                    // Optional: auto-size (disable for big exports)
                    // for (int i = 0; i < columnCount; i++) {
                    //     sheet.autoSizeColumn(i);
                    // }
                }
            }

            workbook.write(out);
        }

        return file;
    }

    private String buildQuery(ExportRequest req, String tableName) {
        StringBuilder query = new StringBuilder("SELECT ");

        if (req.selectedColumns != null && !req.selectedColumns.isEmpty()) {
            query.append(req.selectedColumns.stream()
                    .map(col -> "\"" + col + "\"")
                    .collect(Collectors.joining(", ")));
        } else {
            query.append("*");
        }

        query.append(" FROM ").append(tableName);

        if (req.filters != null && !req.filters.isEmpty()) {
            query.append(" WHERE ");
            List<String> conditions = req.filters.entrySet().stream()
                    .map(entry -> "\"" + entry.getKey() + "\" = '" + entry.getValue() + "'")
                    .collect(Collectors.toList());
            query.append(String.join(" AND ", conditions));
        }

        if (req.sortBy != null && !req.sortBy.isEmpty()) {
            query.append(" ORDER BY \"").append(req.sortBy).append("\" ")
                 .append(req.sortDirection != null ? req.sortDirection : "ASC");
        }

        return query.toString();
    }

    private String resolveTableName(String functionalityName) {
        return FUNCTION_TABLE_MAP.get(functionalityName);
    }

    private String toTitleCase(String columnName) {
        return Arrays.stream(columnName.split("_"))
                .map(word -> word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}