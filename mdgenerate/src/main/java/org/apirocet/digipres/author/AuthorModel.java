package org.apirocet.digipres.author;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "name", "pcms_id", "rights_file" })
public class AuthorModel {

    private String name;
    private int pcms_id;
    private String rights_file;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public int getPcmsId() {
        return pcms_id;
    }

    public void setPcmsId(int pcms_id) {
        this.pcms_id = pcms_id;
    }

    public String getRightsFile() {
        return rights_file;
    }

    public void setRightsFile(String rights_file) {
        this.rights_file = rights_file;
    }

    @Override
    public Object clone() {
        AuthorModel author_clone = new AuthorModel();

        author_clone.setName(this.name);
        author_clone.setPcmsId(this.pcms_id);
        author_clone.setRightsFile(this.rights_file);

        return author_clone;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Name: " + this.name + "\n");
        sb.append("Author PCMS ID: " + this.pcms_id + "\n");
        sb.append("Rights file: " + this.rights_file + "\n");

        return sb.toString();
    }
}
