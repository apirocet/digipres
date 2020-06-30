package org.apirocet.digipres.episode;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apirocet.digipres.SpreadsheetReader;
import org.apirocet.digipres.pcms.PCMSDataMapper;
import org.slf4j.Logger;

import java.util.Date;

import static org.slf4j.LoggerFactory.getLogger;

public class EpisodeMapper {

    private static final Logger LOGGER = getLogger(EpisodeMapper.class);
    private static final PCMSDataMapper pcms = new PCMSDataMapper();

    public EpisodeModel mapRowToEpisode(Row row, int magazine_pcms_id) {
        EpisodeModel episode = new EpisodeModel();

        episode.setPublicFlag(isPublic(row));

        boolean is_in_magazine = isInMagazine(row);
        episode.setInMagazine(is_in_magazine);

        if (is_in_magazine) {
            episode.setMagazinePcmsId(magazine_pcms_id);
            Date magazine_date = getPCMSMagazineDate(magazine_pcms_id);
            episode.setMagazineDate(magazine_date);
        }

        int audio_pcms_id = getAudioPcmsId(row);
        if (audio_pcms_id != 0)
            episode.setPcmsId(audio_pcms_id);

        String title = getTitleFromSpreadsheet(row);
        if ((title == null || title.isEmpty()) && audio_pcms_id != 0)
            title = pcms.getEpisodeTitle(audio_pcms_id);
        episode.setTitle(title);

        Date date = getReleaseDate(row);
        if (date == null && audio_pcms_id !=0)
            date = pcms.getEpisodeReleaseDate(audio_pcms_id);
        episode.setReleaseDate(date);
        
        String exec_producer = getExecProducer(row);
        if (exec_producer != null && ! exec_producer.isEmpty())
            episode.setExecProducer(formatName(getExecProducer(row)));

        String producer = getProducer(row);
        if (producer != null && ! producer.isEmpty())
            episode.setProducer(formatName(getProducer(row)));

        episode.setRightsExpirationDate(getRightsExpirationDate(row));

        String mp3_file = getMP3File(row);
        if (mp3_file != null && ! mp3_file.isEmpty())
            episode.setPublishedMp3File("Final Episodes/" + mp3_file);

        String wav_file = getWAVFile(row);
        if (wav_file != null && ! wav_file.isEmpty())
            episode.setSourceWavFile("Final Episodes/" + wav_file);

        String transcript_file = getTranscriptFile(row);
        if (transcript_file != null && ! transcript_file.isEmpty() && ! transcript_file.equalsIgnoreCase("NA"))
            episode.setTranscriptFile("Transcripts/" + transcript_file);

        int orig_audio_pcms_id = getOriginalAudioPcmsId(row);
        if (orig_audio_pcms_id != 0) {
            episode.setOriginalPcmsId(orig_audio_pcms_id);
            episode.setOriginalReleaseDate(getOriginalReleaseDate(orig_audio_pcms_id));
        }

        return episode;
    }

    private String formatName(String name) {
        if (name.matches(","))
            return name;
        String name_parts[] = name.split(" ", 2);

        if (name_parts.length == 1)
            return name;

        return name_parts[1] + ", " + name_parts[0];
    }

    private boolean isPublic(Row row) {
        DataFormatter df = new DataFormatter();
        Cell cell = row.getCell(SpreadsheetReader.getColumnNameMap().get("Public?"));
        String flag = df.formatCellValue(cell);
        if (flag == null || flag.isEmpty())
            return true;
        if (flag.equalsIgnoreCase("n") || flag.equalsIgnoreCase("no") || flag.equalsIgnoreCase("false"))
            return false;
        else
            return true;
    }

    private boolean isInMagazine(Row row) {
        DataFormatter df = new DataFormatter();
        Cell cell = row.getCell(SpreadsheetReader.getColumnNameMap().get("In magazine?"));
        String flag = df.formatCellValue(cell);
        if (flag == null || flag.isEmpty())
            return false;
        if (flag.equalsIgnoreCase("y") || flag.equalsIgnoreCase("yes") || flag.equalsIgnoreCase("true"))
            return true;
        else
            return false;
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

    private int getOriginalAudioPcmsId(Row row) {
        DataFormatter df = new DataFormatter();
        Cell cell = row.getCell(SpreadsheetReader.getColumnNameMap().get("Original PCMS ID"));
        String id = df.formatCellValue(cell);
        if (id == null || id.isEmpty() || id.equals("NA"))
            return 0;
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException ne) {
            return 0;
        }
    }

    private Date getReleaseDate(Row row) {
        return row.getCell(SpreadsheetReader.getColumnNameMap().get("Date")).getDateCellValue();
    }

    private Date getOriginalReleaseDate(int orig_audio_pcms_id) {
        // Get from PCMS
        return null;
    }

    private String getExecProducer(Row row) {
        return row.getCell(SpreadsheetReader.getColumnNameMap().get("Exec Producer")).getStringCellValue();
    }

    private String getProducer(Row row) {
        return row.getCell(SpreadsheetReader.getColumnNameMap().get("Producer")).getStringCellValue();
    }

    private Date getRightsExpirationDate(Row row) {
        return row.getCell(SpreadsheetReader.getColumnNameMap().get("Date")).getDateCellValue();
    }

    private Date getPCMSMagazineDate(int magazine_pcms_id) {
        // Get from PCMS
        return null;
    }

    private String getMP3File(Row row) {
        return row.getCell(SpreadsheetReader.getColumnNameMap().get("MP3")).getStringCellValue();
    }

    private String getWAVFile(Row row) {
        return row.getCell(SpreadsheetReader.getColumnNameMap().get("WAV")).getStringCellValue();
    }

    private String getTranscriptFile(Row row) {
        return row.getCell(SpreadsheetReader.getColumnNameMap().get("Transcript")).getStringCellValue();
    }

}
