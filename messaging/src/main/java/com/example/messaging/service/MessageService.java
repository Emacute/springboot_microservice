package com.example.messaging.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

import static jdk.internal.net.http.common.Utils.isValidName;

@Slf4j
@Service
public class MessageService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final MessageBrokerService messageBrokerService;

    public MessageService(MessageBrokerService messageBrokerService) {
        this.messageBrokerService = messageBrokerService;
    }

    // ===============================
    // Upload Entry Point
    // ===============================
    public void uploadFile(MultipartFile file) {

        log.info("Starting file processing: {}", file.getOriginalFilename());

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        try (Workbook workbook = createWorkbook(file)) {
            processWorkbook(workbook);
        } catch (Exception e) {
            log.error("Error processing file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to process file");
        }

        log.info("File processed successfully: {}", file.getOriginalFilename());
    }

    // ===============================
    // Create Workbook
    // ===============================
    private Workbook createWorkbook(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        return new XSSFWorkbook(inputStream);
    }

    // ===============================
    // Process All Sheets
    // ===============================
    private void processWorkbook(Workbook workbook) {

        log.info("Workbook contains {} sheets", workbook.getNumberOfSheets());

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            log.info("Processing sheet: {}", sheet.getSheetName());
            processSheet(sheet);
        }
    }

    // ===============================
    // Process Single Sheet
    // ===============================
    private void processSheet(Sheet sheet) {

        Iterator<Row> rows = sheet.iterator();

        if (!rows.hasNext()) {
            log.warn("Sheet {} is empty", sheet.getSheetName());
            return;
        }

        Row headerRow = rows.next();
        Map<String, Integer> columnIndex = extractHeaders(headerRow);

        validateRequiredColumns(columnIndex, sheet.getSheetName());

        while (rows.hasNext()) {
            Row currentRow = rows.next();
            processRow(currentRow, columnIndex);
        }
    }

    // ===============================
    // Extract Headers
    // ===============================
    private Map<String, Integer> extractHeaders(Row headerRow) {
        Map<String, Integer> columnIndex = new HashMap<>();

        for (Cell cell : headerRow) {
            columnIndex.put(
                    getCellValue(cell).toLowerCase(),
                    cell.getColumnIndex()
            );
        }

        log.debug("Extracted headers: {}", columnIndex.keySet());
        return columnIndex;
    }

    // ===============================
    // Validate Required Columns
    // ===============================
    private void validateRequiredColumns(Map<String, Integer> columnIndex,
                                         String sheetName) {

        if (!columnIndex.containsKey("name") ||
                !columnIndex.containsKey("email") ||
                !columnIndex.containsKey("phone")) {

            throw new IllegalArgumentException(
                    "Missing required columns (name, email, phone) in sheet: "
                            + sheetName
            );
        }
    }

    // ===============================
    // Process Single Row
    // ===============================
    private void processRow(Row row, Map<String, Integer> columnIndex) {

        String email = getCellValue(row.getCell(columnIndex.get("email")));

        if (email.isEmpty()) {
            log.debug("Skipping row {} - empty email", row.getRowNum());
            return;
        }
        if (getCellValue(row.getCell(columnIndex.get("name"))).isEmpty()) {
            log.debug("Skipping row {} - empty email", row.getRowNum());
            return;
        }

        if (!isValidEmail(email)) {
            log.warn("Invalid email format at row {}: {}", row.getRowNum(), email);
            return;
        }

        String name = getCellValue(row.getCell(columnIndex.get("name")));
        String phone = getCellValue(row.getCell(columnIndex.get("phone")));

        log.info("Valid row {} → Name={}, Email={}, Phone={}",
                row.getRowNum(), name, email, phone);

        messageBrokerService.sendEmailMessage(email, name);
    }


    // ===============================
    // Email Validator
    // ===============================
    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    // ===============================
    // Safe Cell Value Extractor
    // ===============================
    private String getCellValue(Cell cell) {

        if (cell == null) {
            return "";
        }

        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }
}
