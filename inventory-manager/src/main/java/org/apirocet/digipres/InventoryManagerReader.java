package org.apirocet.digipres;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

public class InventoryManagerReader {

    private static final Logger LOGGER = getLogger(InventoryManagerReader.class);
    private static final int NUMCOLS = 7;

    private File inventoryFile;
    private List<String> columns;

    public InventoryManagerReader(File inventoryFile, List<String> columns) {
        this.inventoryFile = inventoryFile;
        if (columns != null) {
            this.columns = new ArrayList<String>(columns);
        }
    }

    public Integer read() {
        XSSFWorkbook wb;
        try {
            wb = readFile(inventoryFile);
        } catch (IOException|InvalidFormatException ex) {
            LOGGER.error("Cannot open inventory spreadsheet", ex);
            return 1;
        }

        Sheet sh = wb.getSheetAt(0);
        Map<String, Integer> columnMap = setColumnMap(sh);
        boolean atFirstRow = true;

        if (columns != null) {
            List<String> validCols = new ArrayList<>();
            for (String column : columns) {
                if (! columnMap.containsKey(column)) {
                    LOGGER.warn("Spreadsheet does not contain the column '{}'.  Skipping.", column);
                    continue;
                } else {
                    validCols.add(column);
                }
            }

            for (Row row: sh) {
                if (atFirstRow) {
                    atFirstRow = false;
                    continue;
                }
                for (String validCol: validCols) {
                    printColCellValue(validCol, row, columnMap.get(validCol));
                }
            }
        } else {

            for (Row row : sh) {
                if (atFirstRow) {
                    atFirstRow = false;
                    continue;
                }
                for (int i = 0; i < NUMCOLS; i++) {
                    Cell cell = row.getCell(i);
                    printColCellValue(getKeyByValue(columnMap, i), row, i);
                }
                System.out.println();
            }
        }
        return 0;
    }

    private XSSFWorkbook readFile(File filename) throws IOException, InvalidFormatException {
		try (OPCPackage pkg = OPCPackage.open(inventoryFile)) {
			return new XSSFWorkbook(pkg);
		}
	}

    private Map<String, Integer> setColumnMap(Sheet sheet) {
        Map<String, Integer> columnMap = new HashMap<>();
        for (int i = 0; i < NUMCOLS; i++) {
            Cell cell = sheet.getRow(0).getCell(i);
            columnMap.put(cell.getRichStringCellValue().getString(), i);
        }
        return columnMap;
    }

    private static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void printColCellValue(String colLabel, Row row, Integer idx) {
        Cell cell = row.getCell(idx);
        if (cell == null)
            return;
        System.out.print(colLabel + ": ");
        switch (cell.getCellType()) {
            case STRING:
                System.out.println(cell.getRichStringCellValue().getString());
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    System.out.println(cell.getDateCellValue());
                } else {
                    System.out.println(cell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                System.out.println(cell.getBooleanCellValue());
                break;
            case FORMULA:
                System.out.println(cell.getCellFormula());
                break;
            case BLANK:
                System.out.println();
                break;
            default:
                System.out.println();
        }

    }
}
