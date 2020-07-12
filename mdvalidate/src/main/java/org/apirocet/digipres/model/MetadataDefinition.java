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
    private String depends_on;

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

    public void setIsReq(Boolean is_req) {
        this.is_req = is_req;
    }

    public String getDependsOn() {
        return depends_on;
    }

    public void setDependsOn(String depends_on) {
        this.depends_on = depends_on;
    }

    public String toString() {
        return "field = " + mdfield + ", type = " + type + ", isRequired = " + is_req + ", dependsOn = " + depends_on;
    }
}
