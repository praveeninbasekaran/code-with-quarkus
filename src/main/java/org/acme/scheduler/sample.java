@RestController
public class ExportController {

    @Autowired
    private EmployeeExportService service;

    @GetMapping("/export/employees")
    public ResponseEntity<byte[]> exportToExcel() throws Exception {
        byte[] fileContent = service.exportToExcel(service.getAllEmployees());

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=employees.xlsx")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(fileContent);
    }
}

@Service
public class EmployeeExportService {

    public byte[] exportToExcel(List<Employee> employees) throws Exception {
        ExportRequest<Employee> request = new ExportRequest<>();
        request.setData(employees);
        request.setFormat(ExportFormat.EXCEL);
        request.setSortByField("id");
        request.setSortOrder(SortOrder.ASC);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ExportUtil.export(request, out);
            return out.toByteArray();
        }
    }

    public List<Employee> getAllEmployees() {
        return List.of(
            new Employee(1, "Alice", "HR"),
            new Employee(2, "Bob", "Finance"),
            new Employee(3, "Charlie", "Engineering")
        );
    }
}//http://localhost:8080/export/employees