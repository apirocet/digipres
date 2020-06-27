package org.apirocet.digipres.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Poem {

    private String title;
    private List<Author> authors;
    private Date air_date;
    private String mp3_file;
    private String wav_file;
    private int text_pcms_id;
    private int audio_poem_pcms_id;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Author> getAuthors() {
        List<Author> author_list = new ArrayList<Author>();
        for (Author author : this.authors) {
            author_list.add((Author) author.clone());
        }
        return author_list;
    }

    public void setAuthors(List<Author> authors) {
        for (Author author : authors) {
            this.authors.add((Author) author.clone());
        }
    }

    public void addAuthor(Author author) {
        this.authors.add((Author) author.clone());
    }

    public Date getAirDate() {
        return cloneDate(this.air_date);
    }

    public void setAirDate(Date air_date) {
        this.air_date = cloneDate(this.air_date);
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

    public int getTextPcmsId() {
        return this.text_pcms_id;
    }

    public void setTextPcmsId(int text_pcms_id) {
        this.text_pcms_id = text_pcms_id;
    }

    public int getAudioPoemPcmsId() {
        return this.audio_poem_pcms_id;
    }

    public void setAudioPoemPcmsId(int audio_poem_pcms_id) {
        this.audio_poem_pcms_id = audio_poem_pcms_id;
    }

    private Date cloneDate(Date date) {
        if (date != null) {
            return (Date) date.clone();
        } else {
            return null;
        }
    }

    public Object clone() {
        Poem poem_clone = new Poem();

        poem_clone.setTitle(this.title);
        poem_clone.setAuthors(this.authors);
        poem_clone.setAirDate(this.air_date);
        poem_clone.setMp3File(this.mp3_file);
        poem_clone.setWavFile(this.wav_file);
        poem_clone.setTextPcmsId(this.text_pcms_id);
        poem_clone.setAudioPoemPcmsId(this.audio_poem_pcms_id);

        return poem_clone;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Title: " + this.title + "\n");
        if (this.air_date != null)
            sb.append("Air date: " + this.air_date.toString() + "\n");
        sb.append("Poem MP3 file: " + this.mp3_file + "\n");
        sb.append("Source poem WAV file: " + this.wav_file + "\n");
        sb.append("Text PCMS ID: " + this.text_pcms_id + "\n");
        sb.append("Audio PCMS ID: " + this.audio_poem_pcms_id +"\n");
        sb.append("Authors:\n");
        int acount = 0;
        for (Author author : this.authors) {
            acount = acount + 1;
            sb.append("  Author " + acount + "\n");
            sb.append(author.toString().replaceAll("(?m)^", "    "));
        }

        return sb.toString();
    }
}
