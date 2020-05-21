package org.apirocet.digipres;

import org.apirocet.digipres.model.MetadataDefinition;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class MetadataValidator {

    private static final Logger LOGGER = getLogger(MetadataValidator.class);

    private List<MetadataDefinition> mddef_list;
    private Map metadata_doc;
    private File directory;
    private String display_key;
    private String mdkey;
    private MetadataDefinition mdentry;
    private Boolean is_valid = true;

    public MetadataValidator (List<MetadataDefinition> mddef_list, Map metadata_doc, File directory) {
        this.mddef_list = mddef_list;
        this.metadata_doc = metadata_doc;
        this.directory = directory;
    }

    public Boolean validate() {
        for (MetadataDefinition md: mddef_list) {
            display_key = "";
            mdentry = md;
            mdkey = md.getMdfield();
            recurseKeys(mdkey, metadata_doc);
        }
        LOGGER.info("Valid: " + is_valid);
        return is_valid;
    }

    private Object recurseKeys(String mdfield, Object metadata) {
        String[] fields = mdfield.split("\\.", 2);
        display_key = display_key.concat(fields[0]);
        if (fields.length == 1) {
            if (metadata instanceof Map) {
                is_valid = is_valid & validateValue(((Map) metadata).get(fields[0]));
            } else if (metadata instanceof ArrayList) {
                String basekey = display_key;
                for (int i = 0; i < ((ArrayList<Map>)metadata).size(); i++) {
                    int idx = i + 1 ;
                    display_key = basekey + "[" + idx + "]";
                    is_valid = is_valid & validateValue((((ArrayList<Map>)metadata).get(i).get(fields[0])));
                }
            }
        } else {
            display_key = display_key.concat(".");
            recurseKeys(fields[1], ((Map) metadata).get(fields[0]));
        }
        return metadata;
    }

    private Boolean validateValue(Object value) {
        Boolean retval = true;

        if (value instanceof String) {
            retval = runChecks((String) value);
        } else if (value instanceof ArrayList) {
            String basekey = display_key;
            for (int i = 0; i < ((ArrayList<String>)value).size(); i++) {
                int idx = i + 1;
                display_key = basekey + "[" + idx + "]";
                String str = ((ArrayList<String>)value).get(i);
                retval = retval && runChecks(str);
            }
        } else if (value == null) {
            if (mdentry.getIs_req() == true) {
                LOGGER.error("ERROR: " + display_key + ": is missing");
                retval = false;
            } else {
                LOGGER.warn("WARN: " + display_key + ": optional field is missing");
            }
        }
        return retval;
    }

    private Boolean runChecks(String str) {
        return checkHasRequiredValue(str) & checkHasCorrectType(str);
    }

    private Boolean checkHasRequiredValue(String value) {
        if ( value == null || value.isBlank() || value.isEmpty() ) {
            if (mdentry.getIs_req() == true) {
                LOGGER.error("ERROR: " + display_key + ": is empty or missing");
                return false;
            } else {
                LOGGER.warn("WARN: " + display_key + ": optional field is empty or missing");
            }
        }
        return true;
    }

    private Boolean checkHasCorrectType(String value) {
        switch (mdentry.getType()) {
            case "flag":
                if ( ! "true".equals(value) && ! "false".equals(value) ) {
                    LOGGER.error("ERROR: " + display_key + ": '" + value + "' is not a 'true' or 'false' flag");
                    return false;
                }
                break;
            case "date":
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy[-MM[-dd]]");
                try {
                   dateFormatter.parse(value);
                } catch (DateTimeParseException e) {
                    LOGGER.error("ERROR: " + display_key + ": '" + value + "' is not a valid date in YYYY-MM-DD format");
                    return false;
                }
                break;
            case "file":
                if (! Files.isReadable((new File(directory.getPath() + File.separator + value)).toPath())) {
                    LOGGER.error("ERROR: " + display_key + ": '" + value + "' is not a readable file in the archive");
                    return false;
                }
                break;
            default:
                break;
        }
        return true;
    }

}
