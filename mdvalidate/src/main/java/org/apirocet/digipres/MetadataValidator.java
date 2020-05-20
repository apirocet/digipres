package org.apirocet.digipres;

import org.apirocet.digipres.model.MetadataDefinition;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class MetadataValidator {

    private static final Logger LOGGER = getLogger(MetadataValidator.class);

    private List<MetadataDefinition> mddef_list;
    private Map metadata;
    private String mdkey;

    public MetadataValidator (List<MetadataDefinition> mddef_list, Map metadata) {
        this.mddef_list = mddef_list;
        this.metadata = metadata;
    }

    public Boolean validate() {
        for (MetadataDefinition md: mddef_list) {
            mdkey = "";
            recurseKeys(md.getMdfield(), metadata);
        }
        return true;
    }

    private Object recurseKeys(String mdfield, Object metadata) {
        String[] fields = mdfield.split("\\.", 2);
        mdkey = mdkey.concat(fields[0]);
        if (fields.length == 1) {
            if (metadata instanceof Map) {
                processvalue(((Map) metadata).get(fields[0]));
            } else if (metadata instanceof ArrayList) {
                String basekey = mdkey;
                for (int i = 0; i < ((ArrayList<Map>)metadata).size(); i++) {
                    int idx = i + 1 ;
                    mdkey = basekey + "[" + idx + "]";
                    processvalue((((ArrayList<Map>)metadata).get(i).get(fields[0])));
                }
            }
        } else {
            mdkey = mdkey.concat(".");
            recurseKeys(fields[1], ((Map) metadata).get(fields[0]));
        }
        return metadata;
    }

    private void processvalue(Object value) {
        if (value instanceof String) {
            LOGGER.info(mdkey + ":  " + (String)value);
        } else if (value instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList<String>)value).size(); i++) {
                int idx = i + 1;
                String str = ((ArrayList<String>)value).get(i);
                LOGGER.info(mdkey + "[" + idx + "]: " + str);
            }
        } else if (value == null) {
            LOGGER.error(mdkey + ":  MISSING");
        }
    }
}
