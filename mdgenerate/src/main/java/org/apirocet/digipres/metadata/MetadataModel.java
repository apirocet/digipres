package org.apirocet.digipres.metadata;

import org.apirocet.digipres.archiveobject.ArchiveObjectModel;

import java.util.ArrayList;
import java.util.List;

public class MetadataModel {
    private ArrayList<ArchiveObjectModel> archive_objects;

    public MetadataModel() {
        archive_objects = new ArrayList<>();
    }

    public List<ArchiveObjectModel> getArchiveObjects() {
        List<ArchiveObjectModel> ao_list = new ArrayList<ArchiveObjectModel>();
        for (ArchiveObjectModel archiveObject : this.archive_objects) {
            ao_list.add((ArchiveObjectModel) archiveObject.clone());
        }
        return ao_list;
    }

    public void setArchiveObjects(List<ArchiveObjectModel> archive_objects) {
        for (ArchiveObjectModel archive_object: archive_objects) {
            this.archive_objects.add((ArchiveObjectModel) archive_object.clone());
        }
    }

    public void addArchiveObject(ArchiveObjectModel archive_object) {
        this.archive_objects.add((ArchiveObjectModel) archive_object.clone());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ArchiveObjectModel archive_object: archive_objects) {
            sb.append(archive_object.toString().replaceAll("(?m)^", "  "));
        }
        return sb.toString();
    }
}
