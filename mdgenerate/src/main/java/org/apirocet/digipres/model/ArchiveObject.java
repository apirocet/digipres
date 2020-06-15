package org.apirocet.digipres.model;

import java.util.Date;

public class ArchiveObject {
    private Integer pcms_id;
    private String archive_id;
    private String program;
    private Date date_archive_updated;

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
        return (Date) this.date_archive_updated.clone();
    }

    public void setDateArchiveUpdated(Date date_archive_updated) {
        this.date_archive_updated = (Date) date_archive_updated.clone();
    }

    @Override
    public Object clone() {
        ArchiveObject ao_clone = new ArchiveObject();
        ao_clone.setArchiveId(this.archive_id);
        ao_clone.setDateArchiveUpdated(this.date_archive_updated);
        ao_clone.setPcmsId(this.pcms_id);
        ao_clone.setProgram(this.program);

        return ao_clone;
    }
}
