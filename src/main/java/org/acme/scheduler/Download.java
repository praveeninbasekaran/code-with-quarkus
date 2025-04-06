import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jakarta.inject.Inject;
import java.util.List;

@Path("/export")
public class ExcelExportResource {

    @Inject
    StudentRepository studentRepository;

    @Inject
    EmployeeRepository employeeRepository;

    @GET
    @Path("/{type}")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response exportExcel(@PathParam("type") String type) {
        try {
            byte[] excelData;
            switch (type.toLowerCase()) {
                case "student" -> {
                    List<Student> students = studentRepository.listAll();
                    excelData = ExcelExportUtil.exportToExcel(students, "student");
                }
                case "employee" -> {
                    List<Employee> employees = employeeRepository.listAll();
                    excelData = ExcelExportUtil.exportToExcel(employees, "employee");
                }
                default -> throw new WebApplicationException("Unsupported export type: " + type, 400);
            }

            return Response.ok(excelData)
                    .header("Content-Disposition", "attachment; filename=" + type + "_data.xlsx")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to export: " + e.getMessage())
                    .build();
        }
    }
}

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class ExcelExportUtil {

    public static <T> byte[] exportToExcel(List<T> dataList, String tableName) throws IOException {
        if (dataList == null || dataList.isEmpty()) {
            throw new IllegalArgumentException("Data list is empty.");
        }

        List<String> headers = ExcelHeaderConstants.HEADERS.get(tableName.toLowerCase());
        if (headers == null) {
            throw new IllegalArgumentException("No headers found for table: " + tableName);
        }

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(tableName.toUpperCase());
            createHeaderRow(sheet, headers);
            populateDataRows(sheet, dataList, headers);
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private static void createHeaderRow(Sheet sheet, List<String> headers) {
        Row headerRow = sheet.createRow(0);
        int col = 0;
        for (String header : headers) {
            Cell cell = headerRow.createCell(col++);
            cell.setCellValue(header);
        }
    }

    private static <T> void populateDataRows(Sheet sheet, List<T> dataList, List<String> headers) {
        int rowIdx = 1;
        for (T obj : dataList) {
            Row row = sheet.createRow(rowIdx++);
            populateRow(row, obj, headers);
        }
    }

    private static <T> void populateRow(Row row, T obj, List<String> headers) {
        Field[] fields = obj.getClass().getDeclaredFields();
        Map<String, Field> fieldMap = Map.of(); // optionally improve with caching

        for (int col = 0; col < headers.size(); col++) {
            String header = headers.get(col).toLowerCase(); // match field name
            for (Field field : fields) {
                if (field.getName().equalsIgnoreCase(header)) {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(obj);
                        row.createCell(col).setCellValue(value != null ? value.toString() : "");
                    } catch (IllegalAccessException e) {
                        row.createCell(col).setCellValue("ERROR");
                    }
                    break;
                }
            }
        }
    }
}

List<Student> students = studentRepository.listAll();
byte[] excelData = ExcelExportUtil.exportToExcel(students, "student");

// return as HTTP response
return Response.ok(excelData)
        .header("Content-Disposition", "attachment; filename=students.xlsx")
        .build();






