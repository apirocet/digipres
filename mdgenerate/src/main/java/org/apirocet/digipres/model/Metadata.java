package org.apirocet.digipres.model;

import java.util.ArrayList;
import java.util.List;

public class Metadata {
    private ArrayList<ArchiveObject> archive_objects;

    public Metadata() {
        archive_objects = new ArrayList<>();
    }

    public List<ArchiveObject> getArchiveObjects() {
        List<ArchiveObject> ao_list = new ArrayList<ArchiveObject>();
        for (ArchiveObject archiveObject : this.archive_objects) {
            ao_list.add((ArchiveObject) archiveObject.clone());
        }
        return ao_list;
    }

    public void setArchiveObjects(List<ArchiveObject> archive_objects) {
        for (ArchiveObject archive_object: archive_objects) {
            this.archive_objects.add((ArchiveObject) archive_object.clone());
        }
    }

    public void addArchiveObject(ArchiveObject archive_object) {
        this.archive_objects.add((ArchiveObject) archive_object.clone());
    }
}
