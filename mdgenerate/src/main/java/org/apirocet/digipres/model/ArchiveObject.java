package org.apirocet.digipres.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArchiveObject {
    private int mag_pcms_id;
    private String archive_id;
    private String program;
    private Date date_archive_updated;
    private List<Episode> episodes;
    private List<Author> authors;
    private List<Poem> poems;

    public ArchiveObject() {
        this.episodes = new ArrayList<>();
        this.authors = new ArrayList<>();
        this.poems = new ArrayList<>();
    }

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

    public Date getDateArchiveUpdated() {
        if (this.date_archive_updated != null) {
            return (Date) this.date_archive_updated.clone();
        }
        return null;
    }

    public void setDateArchiveUpdated(Date date_archive_updated) {
        if (date_archive_updated != null) {
            this.date_archive_updated = (Date) date_archive_updated.clone();
        } else {
            this.date_archive_updated = null;
        }
    }

    public List<Episode> getEpisodes() {
        List<Episode> episode_list = new ArrayList<Episode>();
        for (Episode episode : this.episodes) {
            episode_list.add((Episode) episode.clone());
        }
        return episode_list;
    }

    public void setEpisodes(List<Episode> episodes) {
        for (Episode episode: episodes) {
            this.episodes.add((Episode) episode.clone());
        }
    }

    public void addEpisode(Episode episode) {
        this.episodes.add((Episode)episode.clone());
    }

    public List<Author> getAuthors() {
        List<Author> author_list = new ArrayList<Author>();
        for (Author author : this.authors) {
            author_list.add((Author) author.clone());
        }
        return author_list;
    }

    public void setAuthors(List<Author> authors) {
        for (Author author: authors) {
            this.authors.add((Author) author.clone());
        }
    }

    public void addAuthor(Author author) {
        this.authors.add((Author)author.clone());
    }

    public List<Poem> getPoems() {
        List<Poem> poem_list = new ArrayList<Poem>();
        for (Poem poem : this.poems) {
            poem_list.add((Poem) poem.clone());
        }
        return poem_list;
    }

    public void setPoems(List<Poem> poems) {
        for (Poem poem: poems) {
            this.poems.add((Poem) poem.clone());
        }
    }

    public void addPoem(Poem poem) {
        this.poems.add((Poem)poem.clone());
    }

    @Override
    public Object clone() {
        ArchiveObject ao_clone = new ArchiveObject();
        ao_clone.setArchiveId(this.archive_id);
        ao_clone.setDateArchiveUpdated(this.date_archive_updated);
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
        if (this.date_archive_updated != null)
            sb.append("  Date updated: " + this.date_archive_updated.toString() +"\n");
        sb.append("  Episodes:\n");
        int ecount =0;
        int acount =0;
        int pcount =0;
        for (Episode episode : this.episodes) {
            ecount = ecount + 1;
            sb.append("    Episode " + ecount + "\n");
            sb.append(episode.toString().replaceAll("(?m)^", "      "));
        }
        sb.append("  Authors:\n");
        for (Author author : this.authors) {
            acount = acount + 1;
            sb.append("    Author " + acount + "\n");
            sb.append(author.toString().replaceAll("(?m)^", "      "));
        }
        sb.append("  Poems:\n");
        for (Poem poem : this.poems) {
            pcount = pcount + 1;
            sb.append("    Poem " + pcount + "\n");
            sb.append(poem.toString().replaceAll("(?m)^", "      "));
        }
        return sb.toString();
    }
}
