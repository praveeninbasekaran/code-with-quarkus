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
-----



package your.package.name;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
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

    // Hardcoded functionality-to-table map (Option 1)
    private static final Map<String, String> FUNCTION_TABLE_MAP = Map.of(
        "userManagement", "users_table",
        "productList", "products_table",
        "transactionSummary", "transactions_table"
    );

    public File exportToExcel(ExportRequest request) throws Exception {
        String tableName = resolveTableName(request.functionalityName);
        if (tableName == null) {
            throw new IllegalArgumentException("Invalid functionality name: " + request.functionalityName);
        }

        File file = File.createTempFile(tableName + "_export_", ".xlsx");

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100);
             FileOutputStream out = new FileOutputStream(file);
             Connection conn = dataSource.getConnection()) {

            conn.setAutoCommit(false);
            Sheet sheet = workbook.createSheet("Export");

            String sql = buildQuery(request, tableName);
            try (PreparedStatement ps = conn.prepareStatement(sql,
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY)) {

                ps.setFetchSize(1000); // enable server-side streaming
                try (ResultSet rs = ps.executeQuery()) {
                    ResultSetMetaData meta = rs.getMetaData();
                    int columnCount = meta.getColumnCount();

                    // Header
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

    private String buildQuery(ExportRequest req, String tableName) {
        StringBuilder query = new StringBuilder("SELECT ");

        if (req.selectedColumns != null && !req.selectedColumns.isEmpty()) {
            query.append(req.selectedColumns.stream()
                    .map(col -> "\"" + col + "\"")  // handle camel/snake case safely
                    .collect(Collectors.joining(", ")));
        } else {
            query.append("*");
        }

        query.append(" FROM ").append(tableName);

        if (req.filters != null && !req.filters.isEmpty()) {
            query.append(" WHERE ");
            List<String> conditions = req.filters.entrySet().stream()
                    .map(entry -> "\"" + entry.getKey() + "\" ILIKE '%" + entry.getValue() + "%'")
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
}


//======

public File exportToExcel(ExportRequest request) throws Exception {
    String tableName = resolveTableName(request.functionalityName);
    if (tableName == null) {
        throw new IllegalArgumentException("Invalid functionality name: " + request.functionalityName);
    }

    // Create temporary Excel file
    File file = File.createTempFile(tableName + "_export_", ".xlsx");

    try (
        SXSSFWorkbook workbook = new SXSSFWorkbook(100); // flush after every 100 rows to save memory
        FileOutputStream out = new FileOutputStream(file);
        Connection conn = dataSource.getConnection()
    ) {
        conn.setAutoCommit(false); // for performance and safety
        Sheet sheet = workbook.createSheet("Export");

        String sql = buildQuery(request, tableName);

        try (
            PreparedStatement ps = conn.prepareStatement(sql,
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY)
        ) {
            ps.setFetchSize(1000); // streaming fetch

            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();

                // === Create header style ===
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

                // === Create data style ===
                CellStyle dataStyle = workbook.createCellStyle();
                dataStyle.setBorderTop(BorderStyle.THIN);
                dataStyle.setBorderBottom(BorderStyle.THIN);
                dataStyle.setBorderLeft(BorderStyle.THIN);
                dataStyle.setBorderRight(BorderStyle.THIN);

                // === Write header row ===
                Row header = sheet.createRow(0);
                for (int i = 1; i <= columnCount; i++) {
                    Cell cell = header.createCell(i - 1);
                    cell.setCellValue(meta.getColumnLabel(i));
                    cell.setCellStyle(headerStyle);
                }

                // === Write data rows ===
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

                // (Optional) Auto-size columns — comment out if performance is impacted
                // for (int i = 0; i < columnCount; i++) {
                //     sheet.autoSizeColumn(i);
                // }
            }
        }

        workbook.write(out); // Write to file
    }

    return file;
}
private String buildQuery(ExportRequest req, String tableName) {
    StringBuilder query = new StringBuilder("SELECT ");

    // Dynamically select only requested columns
    if (req.selectedColumns != null && !req.selectedColumns.isEmpty()) {
        query.append(req.selectedColumns.stream()
                .map(col -> "\"" + col + "\"") // wrap in double quotes for safety
                .collect(Collectors.joining(", ")));
    } else {
        query.append("*");
    }

    query.append(" FROM ").append(tableName);

    // Filters with exact match
    if (req.filters != null && !req.filters.isEmpty()) {
        query.append(" WHERE ");
        List<String> conditions = req.filters.entrySet().stream()
            .map(entry -> "\"" + entry.getKey() + "\" = '" + entry.getValue() + "'")
            .collect(Collectors.toList());
        query.append(String.join(" AND ", conditions));
    }

    // Sorting
    if (req.sortBy != null && !req.sortBy.isEmpty()) {
        query.append(" ORDER BY \"").append(req.sortBy).append("\" ")
             .append(req.sortDirection != null ? req.sortDirection : "ASC");
    }

    return query.toString();
}
////////////////



//----//

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

    // Column name to pretty header label mapping
    private static final Map<String, String> COLUMN_HEADER_MAP = Map.of(
        "rating_overlay_category_id", "Category ID",
        "rating_overlay_category", "Category Name",
        "status", "Status",
        "created_by", "Created By",
        "created_at", "Created At",
        "updated_by", "Updated By",
        "updated_at", "Updated At"
        // Add more mappings as needed
    );

    public File exportToExcel(ExportRequest request) throws Exception {
        String tableName = resolveTableName(request.functionalityName);
        if (tableName == null) {
            throw new IllegalArgumentException("Invalid functionality name: " + request.functionalityName);
        }

        File file = File.createTempFile(tableName + "_export_", ".xlsx");

        try (
            SXSSFWorkbook workbook = new SXSSFWorkbook(100); // flush every 100 rows
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
                ps.setFetchSize(1000); // stream rows from DB

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
                        String displayName = COLUMN_HEADER_MAP.getOrDefault(columnLabel, toTitleCase(columnLabel));
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

                    // Optional: auto-size columns (comment if performance drops for large exports)
                    // for (int i = 0; i < columnCount; i++) {
                    //     sheet.autoSizeColumn(i);
                    // }
                }
            }

            workbook.write(out); // write to disk
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