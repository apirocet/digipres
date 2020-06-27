package org.apirocet.digipres.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Episode {
    private String title;
    private Date date;
    private Date air_date;
    private Date original_date;
    private Date original_air_date;
    private List<Poet> poets;
    private String exec_producer;
    private String producer;
    private Date rights_expiration_date;
    private String published_mp3_file;
    private String source_wav_file;
    private String transcript_pdf_file;
    private Boolean public_flag;
    private Integer pcms_id;
    private Integer original_pcms_id;
    private String original_archive_id;
    private Boolean in_magazine;
    private Date magazine_date;
    private Integer magazine_pcms_id;

    public Episode() {
        this.poets = new ArrayList<>();
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

    public List<Poet> getPoets() {
        List<Poet> poet_list = new ArrayList<Poet>();
        for (Poet poet : this.poets) {
            poet_list.add((Poet) poet.clone());
        }
        return poet_list;
    }

    public void setPoets(List<Poet> poets) {
        for (Poet poet: poets) {
            this.poets.add((Poet) poet.clone());
        }
    }

    public void addPoet(Poet poet) {
        this.poets.add((Poet)poet.clone());
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

    public String getTranscriptPdfFile() {
        return transcript_pdf_file;
    }

    public void setTranscriptPdfFile(String transcript_pdf_file) {
        this.transcript_pdf_file = transcript_pdf_file;
    }

    public Boolean getPublicFlag() {
        return public_flag;
    }

    public void setPublicFlag(Boolean public_flag) {
        this.public_flag = public_flag;
    }

    public Integer getPcmsId() {
        return pcms_id;
    }

    public void setPcmsId(Integer pcms_id) {
        this.pcms_id = pcms_id;
    }

    public Integer getOriginalPcmsId() {
        return original_pcms_id;
    }

    public void setOriginalPcmsId(Integer original_pcms_id) {
        this.original_pcms_id = original_pcms_id;
    }

    public String getOriginalArchiveId() {
        return original_archive_id;
    }

    public void setOriginalArchiveId(String original_archive_id) {
        this.original_archive_id = original_archive_id;
    }

    public Boolean getInMagazine() {
        return in_magazine;
    }

    public void setInMagazine(Boolean in_magazine) {
        this.in_magazine = in_magazine;
    }

    public Date getMagazineDate() {
        return cloneDate(this.magazine_date);
    }

    public void setMagazineDate(Date magazine_date) {
        this.magazine_date = cloneDate(magazine_date);
    }

    public Integer getMagazinePcmsId() {
        return magazine_pcms_id;
    }

    public void setMagazinePcmsId(Integer pcms_id) {
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
        Episode epi_clone = new Episode();
        epi_clone.setTitle(this.title);
        epi_clone.setDate(this.date);
        epi_clone.setAirDate(this.air_date);
        epi_clone.setOriginalDate(this.original_date);
        epi_clone.setOriginalAirDate(this.original_air_date);
        epi_clone.setPoets(this.getPoets());
        epi_clone.setExecProducer(this.exec_producer);
        epi_clone.setProducer(this.producer);
        epi_clone.setRightsExpirationDate(this.rights_expiration_date);
        epi_clone.setPublishedMp3File(this.published_mp3_file);
        epi_clone.setSourceWavFile(this.source_wav_file);
        epi_clone.setTranscriptPdfFile(this.transcript_pdf_file);
        epi_clone.setPublicFlag(this.public_flag);
        epi_clone.setPcmsId(this.pcms_id);
        epi_clone.setOriginalPcmsId(this.original_pcms_id);
        epi_clone.setOriginalArchiveId(this.original_archive_id);
        epi_clone.setInMagazine(this.in_magazine);
        epi_clone.setMagazineDate(this.magazine_date);
        epi_clone.setMagazinePcmsId(this.magazine_pcms_id);

        return epi_clone;
    }
}
