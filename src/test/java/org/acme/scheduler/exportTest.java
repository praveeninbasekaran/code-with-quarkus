@QuarkusTest
class ExportFunctionTest {

    @InjectMock
    ExportService exportService;

    @InjectMock
    ExcelExport excelExport;

    @InjectMock
    RiskAssessmentDetailsUpdatedRepository riskAssessmentDetailsUpdatedRepository;

    @Inject
    ExportFunction exportFunction;

    @Test
    void testExportToRAExcelNew_Success() throws Exception {
        // Arrange
        ExcelRaFilterRequest mockRequest = new ExcelRaFilterRequest();
        mockRequest.setRaId("12345");
        mockRequest.setSelectedColumns(List.of("column1", "column2"));

        // Nested mock objects
        ControlEffectivenessAssessment cea = new ControlEffectivenessAssessment();
        cea.setMitigating1LoD(List.of("m1", "m2"));
        cea.setMitigatingSurveillanceControls(List.of("s1"));
        cea.setMitigating2LoDControls(List.of("l2"));
        
        mockRequest.setControlEffectivenessAssessment(cea);
        mockRequest.setInherentRiskAssessment(List.of("inherent1"));
        mockRequest.setResidualRiskAssessment(List.of("residual1"));

        List<RiskAssessmentDetailsUpdated> mockData = List.of(new RiskAssessmentDetailsUpdated());
        Mockito.when(exportService.fetchRiskDetails("12345")).thenReturn(mockData);
        Mockito.when(riskAssessmentDetailsUpdatedRepository.findAllValues("12345")).thenReturn(List.of("value1"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Workbook workbook = new XSSFWorkbook();
        workbook.write(baos);
        String base64Excel = Base64.getEncoder().encodeToString(baos.toByteArray());

        // Act
        Response response = exportFunction.exportToRAExcelNew(mockRequest);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity().toString().startsWith("UEsDB")); // Base64 for XLSX starts with UEsDB
    }

    @Test
    void testExportToRAExcelNew_NoData() {
        // Arrange
        ExcelRaFilterRequest mockRequest = new ExcelRaFilterRequest();
        mockRequest.setRaId("12345");
        Mockito.when(exportService.fetchRiskDetails("12345")).thenReturn(Collections.emptyList());

        // Act
        Response response = exportFunction.exportToRAExcelNew(mockRequest);

        // Assert
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    void testExportToRAExcelNew_Exception() {
        // Arrange
        ExcelRaFilterRequest mockRequest = new ExcelRaFilterRequest();
        mockRequest.setRaId("12345");
        Mockito.when(exportService.fetchRiskDetails("12345"))
               .thenThrow(new RuntimeException("Simulated Exception"));

        // Act
        Response response = exportFunction.exportToRAExcelNew(mockRequest);

        // Assert
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Failed to generate Excel export"));
    }
}