package org.apirocet.digipres.model;

import com.github.jscancella.hash.StandardHasher;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class BagManager {

    private File bagdir;
    private File srcdir;
    private File metadataFile;
    private Map<String,String> metadataFields = new HashMap<>();
    private StandardHasher algorithm = StandardHasher.MD5;
    private boolean replace = false;
    private boolean verifyWithProfile;


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

    public StandardHasher getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(StandardHasher algorithm) {
        this.algorithm = algorithm;
    }

    public boolean isReplace() {
        return replace;
    }

    public void setReplace(boolean replace) {
        this.replace = replace;
    }

    public boolean isVerifiedWithProfile() { return verifyWithProfile; }

    public void setVerifyWithProfile(boolean verifyWithProfile) { this.verifyWithProfile = verifyWithProfile; }
}
