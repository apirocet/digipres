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
import java.util.Date;
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
    private Map<String, Integer> column_name_map;

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
        int rows = xlssheet.getLastRowNum();
        int counter = 0;
        for (int r = 0; r < rows; r++) {
            counter = r;

            if (r < 2) { //Skip first two rows:  column headers and notes
                if (r == 0) {
                    // generate column name map
                    column_name_map = setColumnMapByName(xlssheet.getRow(r));
                }
                continue;
            }

            Row row = xlssheet.getRow(r);

            // Blank row is end of spreadsheet
            if (isEmptyRow(row)) {
                break;
            }

            Integer pcms_id = getMagazinePCMSID(row);
            // first row
            if (archive_object == null && pcms_id == 0) {
                LOGGER.error("Sheet '" + sheet +"' in file '" + xlsfile + "' does not start off with a Magazine PCMS ID.");
                System.err.println("Sheet '" + sheet +"' in file '" + xlsfile + "' does not start off with a Magazine PCMS ID.  Exiting.");
                System.exit(1);
            } else if (archive_object == null) {
                archive_object = new ArchiveObject();
                archive_object.setPcmsId(pcms_id);
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("Creating new Archive object for Magazine PCMS ID " + pcms_id);
            } else if (pcms_id != 0){ // next archive object
                metadata.addArchiveObject(archive_object);
                archive_object = new ArchiveObject();
                archive_object.setPcmsId(pcms_id);
                archive_object.setDateArchiveUpdated(new Date());
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("Creating new Archive object for Magazine PCMS ID " + pcms_id);
            }

            String audio_type = getAudioType(row);
            switch (audio_type) {
                case "episode":
                    System.out.println("Row is episode");
                    break;
                case "poem":
                    System.out.println("Row is poem");
                    break;
                case "":
                    System.out.println("Audio type is empty");
                    // check and see if this is a poet row for the previous episode/poem
                    break;
                default:
                    LOGGER.warn("Cannot determine row " + counter + " audio type.  Skipping row.");
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
        Integer pcms_id = 0;
        pcms_id = (int) row.getCell(column_name_map.get("Magazine PCMS ID")).getNumericCellValue();
        return pcms_id;
    }

    private String getAudioType(Row row) {
        return row.getCell(column_name_map.get("Audio Type")).getStringCellValue().toLowerCase();
    }

    private Map<String, Integer> setColumnMapByName(Row row) {
        Map<String, Integer> column_name_map = new HashMap<String, Integer>();
        int colNum = row.getLastCellNum();
        if (row.cellIterator().hasNext()) {
            for (int j = 0; j < colNum; j++) {
                column_name_map.put(row.getCell(j).getStringCellValue(), j);
            }
        }

        return column_name_map;
    }
}
