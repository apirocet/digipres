package org.apirocet.digipres.poem;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apirocet.digipres.SpreadsheetReader;
import org.apirocet.digipres.poem.PoemModel;
import org.slf4j.Logger;

import java.util.Date;

import static org.slf4j.LoggerFactory.getLogger;

public class PoemMapper {

    private static final Logger LOGGER = getLogger(PoemMapper.class);

    public PoemModel mapRowToPoem(Row row) {
        PoemModel poem = new PoemModel();

        String title = getTitleFromSpreadsheet(row);
        if (title != null && ! title.isEmpty())
            poem.setTitle(title);
        // otherwise, get title from PCMS

        int audio_pcms_id = getAudioPcmsId(row);
        if (audio_pcms_id != 0)
            poem.setAudioPoemPcmsId(audio_pcms_id);

        Date air_date = getAirDate(row);
        if (air_date != null)
            poem.setAirDate(air_date);
        // otherwise get date from PCMS

        String mp3_file = getMP3File(row);
        if (mp3_file != null && ! mp3_file.isEmpty())
            poem.setMp3File("Audio Poems/" + mp3_file);

        String wav_file = getWAVFile(row);
        if (wav_file != null && ! wav_file.isEmpty())
            poem.setWavFile("Audio Poems/" + wav_file);

        int text_pcms_id = getTextPcmsId(audio_pcms_id);
        if (text_pcms_id != 0)
            poem.setTextPcmsId(text_pcms_id);


        return poem;
    }

    private String getTitleFromSpreadsheet(Row row) {
        return row.getCell(SpreadsheetReader.getColumnNameMap().get("Title")).getStringCellValue();
    }

    private int getAudioPcmsId(Row row) {
        DataFormatter df = new DataFormatter();
        Cell cell = row.getCell(SpreadsheetReader.getColumnNameMap().get("Audio PCMS ID"));
        String id = df.formatCellValue(cell);
        if (id == null || id.isEmpty() || id.equals("NA"))
            return 0;
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException ne) {
            return 0;
        }
    }

    private int getTextPcmsId(int audio_pcms_id) {
       // Get from PCMS
        return 0;
    }

    private Date getAirDate(Row row) {
        return row.getCell(SpreadsheetReader.getColumnNameMap().get("Date")).getDateCellValue();
    }

    private String getMP3File(Row row) {
        return row.getCell(SpreadsheetReader.getColumnNameMap().get("MP3")).getStringCellValue();
    }

    private String getWAVFile(Row row) {
        return row.getCell(SpreadsheetReader.getColumnNameMap().get("WAV")).getStringCellValue();
    }


}
