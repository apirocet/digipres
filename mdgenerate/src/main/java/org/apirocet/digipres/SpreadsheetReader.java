package org.apirocet.digipres;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apirocet.digipres.model.*;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

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
        List<Author> authors = null;
        Episode episode = null;
        Poem poem = null;
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
                if (archive_object != null)
                    if (episode != null)
                        archive_object.addEpisode(episode);
                    if (poem != null)
                        archive_object.addPoem(poem);
                    metadata.addArchiveObject(archive_object);
                break;
            }

            int mag_pcms_id = getMagazinePCMSID(row);
            // first row
            if (archive_object == null && mag_pcms_id == 0) {
                LOGGER.error("Sheet '" + sheet +"' in file '" + xlsfile + "' does not start off with a Magazine PCMS ID.");
                System.err.println("Sheet '" + sheet +"' in file '" + xlsfile + "' does not start off with a Magazine PCMS ID.  Exiting.");
                System.exit(1);
            } else if (mag_pcms_id != 0) { // next archive object
                if (archive_object != null) {
                    if (episode != null)
                        archive_object.addEpisode(episode);
                    if (poem != null)
                        archive_object.addPoem(poem);
                    metadata.addArchiveObject(archive_object);
                }
                archive_object = new ArchiveObject();
                archive_object.setMagazinePcmsId(mag_pcms_id);
                archive_object.setDateArchiveUpdated(new Date());
                episode = null;
                poem = null;
                authors = new ArrayList<>();
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("Creating new Archive object for Magazine PCMS ID " + mag_pcms_id);
            }

            // Process author
            int author_id = getAuthorPCMSID(row);
            String audio_type = getAudioType(row);
            if (author_id != 0) {
                Author author = new Author();
                author.setPcmsId(author_id);
                String rights_file = getAuthorRightsFile(row);
                if (rights_file != null && !rights_file.isEmpty() && !rights_file.equals("NA")) {
                    author.setRightsFile("Rights Data/" + rights_file);
                }

                if (! audio_type.isEmpty()) {
                    authors = new ArrayList<>();
                } else if (episode != null) {
                    episode.addAuthor(author);
                }

                if (authors != null)
                    authors.add(author);

                archive_object.addAuthor(author);
            }

            // Process episodes and poems
            switch (audio_type) {
                case "":
                    break;
                case "episode":
                    if (episode != null)
                        archive_object.addEpisode(episode);
                    episode = new Episode();
                    if (authors != null)
                        episode.setAuthors(authors);
                    episode.setMagazinePcmsId(mag_pcms_id);
                    break;
                case "poem":
                    System.out.println("Row is poem");
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

    private int getMagazinePCMSID(Row row) {
        return (int) row.getCell(column_name_map.get("Magazine PCMS ID")).getNumericCellValue();
    }

    private String getAudioType(Row row) {
        return row.getCell(column_name_map.get("Audio Type")).getStringCellValue().toLowerCase();
    }

    private int getAuthorPCMSID(Row row) {
        return (int) row.getCell(column_name_map.get("Poet PCMS ID")).getNumericCellValue();
    }

    private String getAuthorRightsFile(Row row) {
        return row.getCell(column_name_map.get("Poet Rights File")).getStringCellValue();
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
