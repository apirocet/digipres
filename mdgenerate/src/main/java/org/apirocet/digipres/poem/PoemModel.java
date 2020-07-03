package org.apirocet.digipres.poem;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apirocet.digipres.author.AuthorConverter;
import org.apirocet.digipres.author.AuthorModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonPropertyOrder({ "title", "latest_release_date", "authors", "mp3_file", "wav_file", "pcms_id",
        "text_pcms_id" })
public class PoemModel {

    private String title;
    private List<AuthorModel> authors;
    private Date latest_release_date;
    private String mp3_file;
    private String wav_file;
    private int text_pcms_id;
    private int pcms_id;

    public PoemModel() {
        this.authors = new ArrayList<>();
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    //@JsonIgnoreProperties(value = { "pcms_id", "rights_file" })
    @JsonSerialize(converter = AuthorConverter.class)
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public Date getLatestReleaseDate() {
        return cloneDate(this.latest_release_date);
    }

    public void setLatestReleaseDate(Date latest_release_date) {
        this.latest_release_date = cloneDate(latest_release_date);
    }

    public String getMp3File() {
        return this.mp3_file;
    }

    public void setMp3File(String mp3_file) {
        this.mp3_file = mp3_file;
    }

    public String getWavFile() {
        return this.wav_file;
    }

    public void setWavFile(String wav_file) {
        this.wav_file = wav_file;
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public int getTextPcmsId() {
        return this.text_pcms_id;
    }

    public void setTextPcmsId(int text_pcms_id) {
        this.text_pcms_id = text_pcms_id;
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public int getPcmsId() {
        return this.pcms_id;
    }

    public void setPcmsId(int pcms_id) {
        this.pcms_id = pcms_id;
    }

    private Date cloneDate(Date date) {
        if (date != null) {
            return (Date) date.clone();
        } else {
            return null;
        }
    }

    public Object clone() {
        PoemModel poem_clone = new PoemModel();

        poem_clone.setTitle(this.title);
        poem_clone.setAuthors(this.getAuthors());
        poem_clone.setLatestReleaseDate(this.latest_release_date);
        poem_clone.setMp3File(this.mp3_file);
        poem_clone.setWavFile(this.wav_file);
        poem_clone.setTextPcmsId(this.text_pcms_id);
        poem_clone.setPcmsId(this.pcms_id);

        return poem_clone;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Title: " + this.title + "\n");
        if (this.latest_release_date != null)
            sb.append("Latest release date: " + this.latest_release_date.toString() + "\n");
        sb.append("Poem MP3 file: " + this.mp3_file + "\n");
        sb.append("Source poem WAV file: " + this.wav_file + "\n");
        sb.append("PCMS ID: " + this.pcms_id +"\n");
        sb.append("Text PCMS ID: " + this.text_pcms_id + "\n");
        sb.append("Authors:\n");
        int acount = 0;
        for (AuthorModel author : this.authors) {
            acount = acount + 1;
            sb.append("  Author " + acount + "\n");
            sb.append(author.toString().replaceAll("(?m)^", "    "));
        }

        return sb.toString();
    }
}
