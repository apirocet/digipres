package org.apirocet.digipres;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apirocet.digipres.model.ArchiveObject;
import org.apirocet.digipres.model.Metadata;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Metadata definition file reader and object instantiater.
 *
 * @author Scott Prater
 * @since 2020-06-05
 */
public class SpreadsheetReader {
    private static final Logger LOGGER = getLogger(SpreadsheetReader.class);

    private File xlsfile;
    private String sheet;

    public SpreadsheetReader(File xlsfile, String sheet) {
        this.xlsfile = xlsfile;
        this.sheet = sheet;
    }

    public Metadata getMetadata() {
        Sheet xlssheet = getXLSSheet();

        Metadata metadata = readMetadataFromSpreadsheet(xlssheet);

        return metadata;
    }

    private Sheet getXLSSheet() {

        Sheet xlssheet = null;

        if (xlsfile == null) {
            LOGGER.error("No spreadsheet file specified.");
            System.err.println("No spreadsheet file specified.  Exiting.");
            System.exit(1);
        }

        if (sheet == null || sheet.isEmpty()) {
            LOGGER.error("No sheet name specified.");
            System.err.println("No sheet name specified.  Exiting.");
            System.exit(1);
        }

        if (! Files.isReadable(xlsfile.toPath())) {
            LOGGER.error("Spreadsheet file '{}' is not readable", xlsfile);
            System.err.println("Spreadsheet file '" + xlsfile + "' is not readable.  Exiting.");
            System.exit(1);
        }

        try (FileInputStream fis = new FileInputStream(xlsfile)) {
            Workbook wkbk = new XSSFWorkbook(fis);
            xlssheet = wkbk.getSheet(sheet);

            if (xlssheet == null) {
                LOGGER.error("No such sheet '{}' in file '{}'", sheet, xlsfile);
                System.err.println("No such sheet '" + sheet + "' in file '" + xlsfile + "' .  Exiting.");
                System.exit(1);
            }

        } catch (IOException ex) {
            LOGGER.error("Spreadsheet file '{}' cannot be loaded: {}", xlsfile, ex.getMessage());
            System.err.println("Spreadsheet file '" + xlsfile + "' cannot be loaded.  Exiting.");
            System.exit(1);
        }

        return xlssheet;
    }

    private Metadata readMetadataFromSpreadsheet(Sheet xlssheet) {
        Metadata metadata = new Metadata();
        ArchiveObject archive_object = null;
        Map column_name_map = null;
        int rows = xlssheet.getLastRowNum();
        int counter = 0;
        for (int r = 0; r < rows; r++) {
            counter = r;
            if (r < 3) { //Skip first two rows:  column headers and notes
                if (r == 0) {
                    // generate column name map
                    column_name_map = setColumnMapByName(xlssheet.getRow(r));
                    System.out.println(column_name_map);
                }
                continue;
            }

            Row row = xlssheet.getRow(r);
            if (archive_object == null && getMagazinePCMSID(row) == null) {
                LOGGER.error("Sheet '" + sheet +"' in file '" + xlsfile + "' does not start off with a Magazine PCMS ID.");
                System.err.println("Sheet '" + sheet +"' in file '" + xlsfile + "' does not start off with a Magazine PCMS ID.  Exiting.");
                System.exit(1);
            }
            
            if (isEmptyRow(row)) {
                break;
            }
        }
        System.out.println("Sheet " + sheet + " has " +  counter + " row(s).");

        return metadata;
    }

    private boolean isEmptyRow(Row row) {
        if (row == null) {
            return true;
        }
        if (row.getLastCellNum() <= 0) {
            return true;
        }
        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK && ! cell.toString().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private Integer getMagazinePCMSID(Row row) {
        Integer pcms_id = null;

        return pcms_id;
    }

    private Map<String, Integer> setColumnMapByName(Row row) {
        Map<String, Integer> column_name_map = new HashMap<String, Integer>();
        int colNum = row.getLastCellNum();
        if (row.cellIterator().hasNext()) {
            for (int j = 0; j < colNum; j++) {
                column_name_map.put(row.getCell(j).getStringCellValue(), j);
            }
        }

        System.out.println(column_name_map);

        return column_name_map;
    }
}
