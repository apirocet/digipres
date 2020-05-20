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

    public MetadataValidator (List<MetadataDefinition> mddef_list, Map metadata) {
        this.mddef_list = mddef_list;
        this.metadata = metadata;
    }

    public Boolean validate() {
        for (MetadataDefinition md: mddef_list) {
            getValue(md.getMdfield(), metadata);
        }
        return true;
    }

    private Object getValue(String mdfield, Object metadata) {
        String[] fields = mdfield.split("\\.", 2);
        if (fields.length == 1) {
            if (metadata instanceof Map) {
                LOGGER.info(fields[0] + ": ");
                processvalue(((Map) metadata).get(fields[0]));
            } else if (metadata instanceof ArrayList) {
                for (Map map : (ArrayList<Map>)metadata) {
                    LOGGER.info(fields[0] + ": ");
                    processvalue(map.get(fields[0]));
                }
            }
        } else {
            getValue(fields[1], ((Map) metadata).get(fields[0]));
        }
        return metadata;
    }

    private void processvalue(Object value) {
        if (value instanceof String) {
            LOGGER.info("  " + (String)value);
        } else if (value instanceof ArrayList) {
            for (String str : (ArrayList<String>) value) {
                LOGGER.info("  - " + str);
            }
        }
    }
}
