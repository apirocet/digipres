package org.apirocet.digipres;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apirocet.digipres.archiveobject.ArchiveObjectModel;
import org.apirocet.digipres.archiveobject.ArchiveObjectMapper;
import org.apirocet.digipres.author.AuthorModel;
import org.apirocet.digipres.author.AuthorMapper;
import org.apirocet.digipres.episode.EpisodeMapper;
import org.apirocet.digipres.episode.EpisodeModel;
import org.apirocet.digipres.metadata.MetadataModel;
import org.apirocet.digipres.poem.PoemMapper;
import org.apirocet.digipres.poem.PoemModel;
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
    private static Map<String, Integer> column_name_map;
    private static String program;

    public SpreadsheetReader(File xlsfile, String sheet, String program) {
        this.xlsfile = xlsfile;
        this.sheet = sheet;
        this.program = program;
    }

    public MetadataModel getMetadata() {
        Sheet xlssheet = getXLSSheet();

        MetadataModel metadata = readMetadataFromSpreadsheet(xlssheet);

        return metadata;
    }

    public static Map<String, Integer> getColumnNameMap() {
        return column_name_map;
    }

    public static String getProgram() {
        return program;
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

    private MetadataModel readMetadataFromSpreadsheet(Sheet xlssheet) {
        MetadataModel metadata = new MetadataModel();
        ArchiveObjectModel archive_object = null;
        List<AuthorModel> authors = null;
        EpisodeModel episode = null;
        PoemModel poem = null;
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
                ArchiveObjectMapper aom = new ArchiveObjectMapper();
                archive_object = aom.mapRowToArchiveObject(mag_pcms_id);

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
                AuthorMapper am = new AuthorMapper();
                AuthorModel author = am.mapRowToAuthor(row, author_id);

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
                    EpisodeMapper em = new EpisodeMapper();
                    episode = em.mapRowToEpisode(row, archive_object.getMagazinePcmsId(), archive_object.getMagazineDate());
                    if (authors != null)
                        episode.setAuthors(authors);
                    break;
                case "poem":
                    if (poem != null)
                        archive_object.addPoem(poem);
                    PoemMapper pm = new PoemMapper();
                    poem = pm.mapRowToPoem(row);
                    if (authors != null)
                        poem.setAuthors(authors);
                    break;
                default:
                    LOGGER.warn("Cannot determine row " + counter + " audio type.  Skipping row.");
            }

        }

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Sheet " + sheet + " has " +  counter + " row(s).");

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

    private String getAudioType(Row row) {
        return row.getCell(column_name_map.get("Audio Type")).getStringCellValue().toLowerCase();
    }

    private int getMagazinePCMSID(Row row) {
        return (int) row.getCell(column_name_map.get("Magazine PCMS ID")).getNumericCellValue();
    }

    private int getAuthorPCMSID(Row row) {
        return (int) row.getCell(column_name_map.get("Poet PCMS ID")).getNumericCellValue();
    }

    private static Map<String, Integer> setColumnMapByName(Row row) {
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
