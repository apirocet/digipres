package org.apirocet.digipres;

import org.apirocet.digipres.model.MetadataDefinition;
import org.slf4j.Logger;

import javax.management.ImmutableDescriptor;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Metadata definition file reader and object instantiater.
 *
 * @author Scott Prater
 * @since 2020-04-05
 */
public class MetadataDefinitionReader {

    private static final Logger LOGGER = getLogger(MetadataDefinitionReader.class);

    private File mddeffile;
    private boolean verifyWithProfile;
    public MetadataDefinitionReader(File mddeffile) {
        this.mddeffile = mddeffile;
    }

    public List<MetadataDefinition> loadMetadataDefinition() {


        if (mddeffile == null) {
            LOGGER.error("No metadata definition file specified.");
            System.err.println("No metadata definition file specified.  Exiting.");
            System.exit(1);
        }

        if (! Files.isReadable(mddeffile.toPath())) {
            LOGGER.error("Metadata file '{}' is not readable", mddeffile);
            System.err.println("Metadata file '" + mddeffile + "' is not readable.  Exiting.");
            System.exit(1);
        }
        Scanner mdscanner = null;
        try {
            mdscanner = new Scanner(mddeffile);
        } catch (IOException ex) {
            LOGGER.error("Metadata file '{}' cannot be loaded: {}", mddeffile, ex.getMessage());
            System.err.println("Metadata file '" + mddeffile + "' cannot be loaded.  Exiting.");
            System.exit(1);
        }

        return parseDefinitionFile(mdscanner);
    }

    private List<MetadataDefinition> parseDefinitionFile(Scanner mdscanner) {

        List<MetadataDefinition> mddef = new ArrayList<>();

        while (mdscanner.hasNextLine()) {
            String line = mdscanner.nextLine();
            if (line.startsWith("#"))
                continue;

            Scanner lineScanner = new Scanner(line);
            lineScanner.useDelimiter("\\|");

            MetadataDefinition mdobj = new MetadataDefinition();

            if (lineScanner.hasNext())
                mdobj.setMdfield(lineScanner.next());
            if (lineScanner.hasNext())
                mdobj.setType(lineScanner.next());
            if (lineScanner.hasNext())
                mdobj.setIs_req(lineScanner.next().equalsIgnoreCase("yes"));
            if (lineScanner.hasNext())
                mdobj.setDep_mdfield(lineScanner.next());

            mddef.add(mdobj);
        }

        return mddef;
    }
}
