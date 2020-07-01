package org.apirocet.digipres.archiveobject;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apirocet.digipres.author.AuthorModel;
import org.apirocet.digipres.episode.EpisodeModel;
import org.apirocet.digipres.poem.PoemModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonPropertyOrder({ "archive_id", "program", "episodes", "poems", "authors", "dates_archive_updated" })
public class ArchiveObjectModel {
    private int mag_pcms_id;
    private String archive_id;
    private String program;
    private List<Date> dates_archive_updated;
    private Date magazine_date;
    private List<EpisodeModel> episodes;
    private List<AuthorModel> authors;
    private List<PoemModel> poems;

    public ArchiveObjectModel() {
        this.episodes = new ArrayList<>();
        this.authors = new ArrayList<>();
        this.poems = new ArrayList<>();
        this.dates_archive_updated = new ArrayList<>();
    }

    @JsonIgnore
    public int getMagazinePcmsId() {
        return this.mag_pcms_id;
    }

    public void setMagazinePcmsId(int pcms_id) {
        this.mag_pcms_id = pcms_id;
    }

    public String getArchiveId() {
        return this.archive_id;
    }

    public void setArchiveId(String archive_id) {
        this.archive_id = archive_id;
    }

    public String getProgram() {
        return this.program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    @JsonIgnore
    public Date getMagazineDate() {
        if (this.magazine_date != null) {
            return (Date) this.magazine_date.clone();
        }
        return null;
    }

    public void setMagazineDate(Date magazine_date) {
        if (magazine_date != null) {
            this.magazine_date = (Date) magazine_date.clone();
        } else {
            this.magazine_date = null;
        }
    }

    public List<EpisodeModel> getEpisodes() {
        List<EpisodeModel> episode_list = new ArrayList<EpisodeModel>();
        for (EpisodeModel episode : this.episodes) {
            episode_list.add((EpisodeModel) episode.clone());
        }
        return episode_list;
    }

    public void setEpisodes(List<EpisodeModel> episodes) {
        for (EpisodeModel episode: episodes) {
            this.episodes.add((EpisodeModel) episode.clone());
        }
    }

    public void addEpisode(EpisodeModel episode) {
        this.episodes.add((EpisodeModel)episode.clone());
    }

    public List<AuthorModel> getAuthors() {
        List<AuthorModel> author_list = new ArrayList<AuthorModel>();
        for (AuthorModel author : this.authors) {
            author_list.add((AuthorModel) author.clone());
        }
        return author_list;
    }

    public void setAuthors(List<AuthorModel> authors) {
        for (AuthorModel author: authors) {
            this.authors.add((AuthorModel) author.clone());
        }
    }

    public void addAuthor(AuthorModel author) {
        this.authors.add((AuthorModel)author.clone());
    }

    public List<PoemModel> getPoems() {
        List<PoemModel> poem_list = new ArrayList<PoemModel>();
        for (PoemModel poem : this.poems) {
            poem_list.add((PoemModel) poem.clone());
        }
        return poem_list;
    }

    public void setPoems(List<PoemModel> poems) {
        for (PoemModel poem: poems) {
            this.poems.add((PoemModel) poem.clone());
        }
    }

    public void addPoem(PoemModel poem) {
        this.poems.add((PoemModel)poem.clone());
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public List<Date> getDatesArchiveUpdated() {
        List<Date> date_list = new ArrayList<Date>();
        for (Date date : this.dates_archive_updated) {
            if (date != null)
                date_list.add((Date) date.clone());
        }
        return date_list;
    }

    public void setDatesArchiveUpdated(List<Date> dates_archive_updated) {
        for (Date date: dates_archive_updated) {
            if (date != null)
                this.dates_archive_updated.add((Date) date.clone());
        }
    }

    public void addDatesArchiveUpdated(Date date) {
        if (date != null)
        this.dates_archive_updated.add((Date)date.clone());
    }

    @Override
    public Object clone() {
        ArchiveObjectModel ao_clone = new ArchiveObjectModel();
        ao_clone.setArchiveId(this.archive_id);
        ao_clone.setDatesArchiveUpdated(this.dates_archive_updated);
        ao_clone.setMagazineDate(this.magazine_date);
        ao_clone.setMagazinePcmsId(this.mag_pcms_id);
        ao_clone.setProgram(this.program);
        ao_clone.setEpisodes(this.episodes);
        ao_clone.setAuthors(this.authors);
        ao_clone.setPoems(this.poems);

        return ao_clone;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("ArchiveID: " + this.archive_id +"\n");
        sb.append("  Program: " + this.program + "\n");
        sb.append("  Magazine PCMS ID: " + this.mag_pcms_id + "\n");
        int dcount =0;
        sb.append("  Dates archive updated:\n");
        for (Date date : this.dates_archive_updated) {
            dcount = dcount + 1;
            if (date != null)
                sb.append("    Date " + dcount + "\n");
                sb.append(date.toString().replaceAll("(?m)^", "      "));
                sb.append("\n");
        }
        if (this.magazine_date != null)
            sb.append("  Magazine date: " + this.magazine_date + "\n");
        sb.append("  Episodes:\n");
        int ecount =0;
        int acount =0;
        int pcount =0;
        for (EpisodeModel episode : this.episodes) {
            ecount = ecount + 1;
            sb.append("    Episode " + ecount + "\n");
            sb.append(episode.toString().replaceAll("(?m)^", "      "));
        }
        sb.append("  Poems:\n");
        for (PoemModel poem : this.poems) {
            pcount = pcount + 1;
            sb.append("    Poem " + pcount + "\n");
            sb.append(poem.toString().replaceAll("(?m)^", "      "));
        }
        sb.append("  Authors:\n");
        for (AuthorModel author : this.authors) {
            acount = acount + 1;
            sb.append("    Author " + acount + "\n");
            sb.append(author.toString().replaceAll("(?m)^", "      "));
        }
        return sb.toString();
    }
}
