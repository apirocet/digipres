package org.apirocet.digipres.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArchiveObject {
    private Integer pcms_id;
    private String archive_id;
    private String program;
    private Date date_archive_updated;
    private List<Episode> episodes;

    public ArchiveObject() {
        this.episodes = new ArrayList<>();
    }

    public Integer getPcmsId() {
        return this.pcms_id;
    }

    public void setPcmsId(Integer pcms_id) {
        this.pcms_id = pcms_id;
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

    @Override
    public Object clone() {
        ArchiveObject ao_clone = new ArchiveObject();
        ao_clone.setArchiveId(this.archive_id);
        ao_clone.setDateArchiveUpdated(this.date_archive_updated);
        ao_clone.setPcmsId(this.pcms_id);
        ao_clone.setProgram(this.program);
        ao_clone.setEpisodes(this.episodes);

        return ao_clone;
    }
}
