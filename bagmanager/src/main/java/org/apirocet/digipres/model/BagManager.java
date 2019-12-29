package org.apirocet.digipres.model;

import gov.loc.repository.bagit.hash.StandardSupportedAlgorithms;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class BagManager {

    private File bagdir;
    private File srcdir;
    private File metadataFile;
    private Map<String,String> metadataFields = new HashMap<>();
    private StandardSupportedAlgorithms algorithm = StandardSupportedAlgorithms.MD5;
    private boolean replace = false;


    public File getBagdir() {
        return bagdir;
    }

    public void setBagdir(File bagdir) {
        this.bagdir = bagdir;
    }

    public File getSrcdir() {
        return srcdir;
    }

    public void setSrcdir(File srcdir) {
        this.srcdir = srcdir;
    }

    public File getMetadataFile() {
        return metadataFile;
    }

    public void setMetadataFile(File metadataFile) {
        this.metadataFile = metadataFile;
    }

    public Map<String, String> getMetadataFields() {
        return metadataFields;
    }

    public void setMetadataFields(Map<String, String> metadataFields) {
        this.metadataFields = metadataFields;
    }

    public StandardSupportedAlgorithms getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(StandardSupportedAlgorithms algorithm) {
        this.algorithm = algorithm;
    }

    public boolean isReplace() {
        return replace;
    }

    public void setReplace(boolean replace) {
        this.replace = replace;
    }

}
