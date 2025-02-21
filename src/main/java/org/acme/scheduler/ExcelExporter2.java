import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ExcelExporter {
    public static byte[] exportToExcel(List<RiskDetails> data, List<String> columns) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Risk Details Export");

        // Style for bold headers
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        // Create header row
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns.get(i));
            cell.setCellStyle(headerStyle);
        }

        // Populate data rows
        int rowNum = 1;
        for (RiskDetails dataItem : data) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;
            for (String column : columns) {
                Cell cell = row.createCell(colNum++);
                setCellValue(cell, dataItem, column);
            }
        }

        // Auto-size columns
        for (int i = 0; i < columns.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to byte array and close
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            workbook.close();
            return baos.toByteArray();
        }
    }

    private static void setCellValue(Cell cell, RiskDetails data, String column) {
        switch (column.toLowerCase()) {
            case "ra_id":
                cell.setCellValue(data.raId != null ? data.raId : "");
                break;
            case "risk_behaviour":
                cell.setCellValue(data.riskBehaviour != null ? data.riskBehaviour : "");
                break;
            case "unit_name":
                cell.setCellValue(data.unitName != null ? data.unitName : "");
                break;
            case "assessment_stage":
                cell.setCellValue(data.assessmentStage != null ? data.assessmentStage : "");
                break;
            case "meta_version_number":
                cell.setCellValue(data.metaVersionNumber != null ? data.metaVersionNumber : 0.0);
                break;
            case "risk_assessment_details_id":
                cell.setCellValue(data.riskAssessmentDetailsId != null ? data.riskAssessmentDetailsId : 0);
                break;
            case "stage":
                cell.setCellValue(data.stage != null ? data.stage : "");
                break;
            case "validated":
                cell.setCellValue(data.validated != null ? data.validated.toString() : "false");
                break;
            default:
                // Handle jsonb fields (parse String value into JsonNode)
                if (data.value != null) {
                    com.fasterxml.jackson.databind.JsonNode jsonNode = data.parseValue();
                    if (jsonNode.has(column)) {
                        com.fasterxml.jackson.databind.JsonNode jsonValue = jsonNode.get(column);
                        if (jsonValue.isTextual()) {
                            cell.setCellValue(jsonValue.asText());
                        } else if (jsonValue.isBoolean()) {
                            cell.setCellValue(jsonValue.asBoolean());
                        } else if (jsonValue.isNumber()) {
                            cell.setCellValue(jsonValue.asDouble());
                        } else {
                            cell.setCellValue(jsonValue.toString());
                        }
                    } else {
                        cell.setCellValue("");
                    }
                } else {
                    cell.setCellValue("");
                }
                break;
        }
    }
}