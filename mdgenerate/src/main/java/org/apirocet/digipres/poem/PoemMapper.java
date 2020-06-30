package org.apirocet.digipres.poem;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apirocet.digipres.SpreadsheetReader;
import org.apirocet.digipres.pcms.PCMSDataMapper;
import org.apirocet.digipres.poem.PoemModel;
import org.slf4j.Logger;

import java.util.Date;

import static org.slf4j.LoggerFactory.getLogger;

public class PoemMapper {

    private static final Logger LOGGER = getLogger(PoemMapper.class);
    private static final PCMSDataMapper pcms = new PCMSDataMapper();

    public PoemModel mapRowToPoem(Row row) {
        PoemModel poem = new PoemModel();


        int audio_pcms_id = getAudioPcmsId(row);
        if (audio_pcms_id != 0)
            poem.setAudioPoemPcmsId(audio_pcms_id);

        String title = getTitleFromSpreadsheet(row);
        if ((title == null || title.isEmpty()) && audio_pcms_id != 0)
            title = pcms.getAudioTitle(audio_pcms_id);
        poem.setTitle(title);

        Date latest_release_date = getLatestReleaseDate(row);
        if (latest_release_date == null && audio_pcms_id != 0)
            latest_release_date = pcms.getAudioReleaseDate(audio_pcms_id);
        poem.setLatestReleaseDate(latest_release_date);

        String mp3_file = getMP3File(row);
        if (mp3_file != null && ! mp3_file.isEmpty())
            poem.setMp3File("Audio Poems/" + mp3_file);

        String wav_file = getWAVFile(row);
        if (wav_file != null && ! wav_file.isEmpty())
            poem.setWavFile("Audio Poems/" + wav_file);

        int text_pcms_id = pcms.getAudioTextPoemPcmsId(audio_pcms_id);
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

    private Date getLatestReleaseDate(Row row) {
        return row.getCell(SpreadsheetReader.getColumnNameMap().get("Date")).getDateCellValue();
    }

    private String getMP3File(Row row) {
        return row.getCell(SpreadsheetReader.getColumnNameMap().get("MP3")).getStringCellValue();
    }

    private String getWAVFile(Row row) {
        return row.getCell(SpreadsheetReader.getColumnNameMap().get("WAV")).getStringCellValue();
    }


}
