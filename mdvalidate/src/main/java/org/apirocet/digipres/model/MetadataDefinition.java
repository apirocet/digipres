package org.apirocet.digipres.model;

/**
 * Model for the metadata definition file.
 * - See README.md for usage details
 *
 * @author Scott Prater
 * @since 2020-04-05
 */
public class MetadataDefinition {
    private String mdfield;
    private String type;
    private Boolean is_req;
    private String dep_mdfield;

    public String getMdfield() {
        return mdfield;
    }

    public void setMdfield(String mdfield) {
        this.mdfield = mdfield;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIs_req() {
        return is_req;
    }

    public void setIs_req(Boolean is_req) {
        this.is_req = is_req;
    }

    public String getDep_mdfield() {
        return dep_mdfield;
    }

    public void setDep_mdfield(String dep_mdfield) {
        this.dep_mdfield = dep_mdfield;
    }

    public String toString() {
        return "field = " + mdfield + ", type = " + type + ", isRequired = " + is_req + ", dependent on = " + dep_mdfield;
    }
}
