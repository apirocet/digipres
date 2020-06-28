package org.apirocet.digipres.episode;

import org.apirocet.digipres.author.AuthorModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EpisodeModel {
    private String title;
    private Date date;
    private Date air_date;
    private Date original_date;
    private Date original_air_date;
    private List<AuthorModel> authors;
    private String exec_producer;
    private String producer;
    private Date rights_expiration_date;
    private String published_mp3_file;
    private String source_wav_file;
    private String transcript_file;
    private boolean public_flag;
    private int pcms_id;
    private int original_pcms_id;
    private String original_archive_id;
    private boolean in_magazine;
    private Date magazine_date;
    private int magazine_pcms_id;

    public EpisodeModel() {
        this.authors = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return cloneDate(this.date);
    }

    public void setDate(Date date) {
        this.date = cloneDate(date);
    }

    public Date getAirDate() {
        return cloneDate(this.air_date);
    }

    public void setAirDate(Date air_date) {
        this.air_date = cloneDate(air_date);
    }

    public Date getOriginalDate() {
        return cloneDate(this.original_date);
    }

    public void setOriginalDate(Date date) {
        this.original_date = cloneDate(date);
    }

    public Date getOriginalAirDate() {
        return cloneDate(this.original_air_date);
    }

    public void setOriginalAirDate(Date original_air_date) {
        this.original_air_date = cloneDate(original_air_date);
    }

    public List<AuthorModel> getAuthors() {
        List<AuthorModel> author_list = new ArrayList<AuthorModel>();
        for (AuthorModel author : this.authors) {
            author_list.add((AuthorModel) author.clone());
        }
        return author_list;
    }

    public void setAuthors(List<AuthorModel> authors) {
        for (AuthorModel author : authors) {
            this.authors.add((AuthorModel) author.clone());
        }
    }

    public void addAuthor(AuthorModel author) {
        this.authors.add((AuthorModel) author.clone());
    }

    public String getExecProducer() {
        return exec_producer;
    }

    public void setExecProducer(String exec_producer) {
        this.exec_producer = exec_producer;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public Date getRightsExpirationDate() {
        return cloneDate(this.rights_expiration_date);
    }

    public void setRightsExpirationDate(Date rights_expiration_date) {
        this.rights_expiration_date = cloneDate(rights_expiration_date);
    }

    public String getPublishedMp3File() {
        return published_mp3_file;
    }

    public void setPublishedMp3File(String published_mp3_file) {
        this.published_mp3_file = published_mp3_file;
    }

    public String getSourceWavFile() {
        return source_wav_file;
    }

    public void setSourceWavFile(String source_wav_file) {
        this.source_wav_file = source_wav_file;
    }

    public String getTranscriptFile() {
        return transcript_file;
    }

    public void setTranscriptFile(String transcript_file) {
        this.transcript_file = transcript_file;
    }

    public boolean getPublicFlag() {
        return public_flag;
    }

    public void setPublicFlag(boolean public_flag) {
        this.public_flag = public_flag;
    }

    public int getPcmsId() {
        return pcms_id;
    }

    public void setPcmsId(int pcms_id) {
        this.pcms_id = pcms_id;
    }

    public int getOriginalPcmsId() {
        return original_pcms_id;
    }

    public void setOriginalPcmsId(int original_pcms_id) {
        this.original_pcms_id = original_pcms_id;
    }

    public String getOriginalArchiveId() {
        return original_archive_id;
    }

    public void setOriginalArchiveId(String original_archive_id) {
        this.original_archive_id = original_archive_id;
    }

    public boolean getInMagazine() {
        return in_magazine;
    }

    public void setInMagazine(boolean in_magazine) {
        this.in_magazine = in_magazine;
    }

    public Date getMagazineDate() {
        return cloneDate(this.magazine_date);
    }

    public void setMagazineDate(Date magazine_date) {
        this.magazine_date = cloneDate(magazine_date);
    }

    public int getMagazinePcmsId() {
        return magazine_pcms_id;
    }

    public void setMagazinePcmsId(int magazine_pcms_id) {
        this.magazine_pcms_id = magazine_pcms_id;
    }

    private Date cloneDate(Date date) {
        if (date != null) {
            return (Date) date.clone();
        } else {
            return null;
        }
    }

    @Override
    public Object clone() {
        EpisodeModel epi_clone = new EpisodeModel();
        epi_clone.setTitle(this.title);
        epi_clone.setDate(this.date);
        epi_clone.setAirDate(this.air_date);
        epi_clone.setOriginalDate(this.original_date);
        epi_clone.setOriginalAirDate(this.original_air_date);
        epi_clone.setAuthors(this.getAuthors());
        epi_clone.setExecProducer(this.exec_producer);
        epi_clone.setProducer(this.producer);
        epi_clone.setRightsExpirationDate(this.rights_expiration_date);
        epi_clone.setPublishedMp3File(this.published_mp3_file);
        epi_clone.setSourceWavFile(this.source_wav_file);
        epi_clone.setTranscriptFile(this.transcript_file);
        epi_clone.setPublicFlag(this.public_flag);
        epi_clone.setPcmsId(this.pcms_id);
        epi_clone.setOriginalPcmsId(this.original_pcms_id);
        epi_clone.setOriginalArchiveId(this.original_archive_id);
        epi_clone.setInMagazine(this.in_magazine);
        epi_clone.setMagazineDate(this.magazine_date);
        epi_clone.setMagazinePcmsId(this.magazine_pcms_id);

        return epi_clone;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Title: " + this.title + "\n");
        if (this.date != null)
            sb.append("Date: " + this.date.toString() + "\n");
        if (this.air_date != null)
            sb.append("Air date: " + this.air_date.toString() + "\n");
        if (this.original_date != null)
            sb.append("Air date: " + this.original_date.toString() + "\n");
        if (this.original_air_date != null)
            sb.append("Air date: " + this.original_air_date.toString() + "\n");
        sb.append("Authors:\n");
        int acount = 0;
        for (AuthorModel author : this.authors) {
            acount = acount + 1;
            sb.append("  Author " + acount + "\n");
            sb.append(author.toString().replaceAll("(?m)^", "    "));
        }
        sb.append("Executive producer: " + this.exec_producer + "\n");
        sb.append("Producer: " + this.producer + "\n");
        if (this.rights_expiration_date != null)
            sb.append("Rights expiration date: " + this.rights_expiration_date.toString() + "\n");
        sb.append("Published MP3 file: " + this.published_mp3_file + "\n");
        sb.append("Source WAV file: " + this.source_wav_file + "\n");
        sb.append("Transcript file: " + this.transcript_file + "\n");
        sb.append("Is public? " + this.public_flag + "\n");
        sb.append("PCMS ID: " + this.pcms_id + "\n");
        sb.append("Original PCMS ID: " + this.original_pcms_id + "\n");
        sb.append("Original archive ID: " + this.original_archive_id + "\n");
        sb.append("In magazine? " + this.in_magazine + "\n");
        if (this.in_magazine) {
            if (this.magazine_date != null)
                sb.append("Magazine date: " + this.magazine_date + "\n");
            sb.append("Magazine PCMS ID: " + this.magazine_pcms_id + "\n");
        }

        return sb.toString();
    }
}
