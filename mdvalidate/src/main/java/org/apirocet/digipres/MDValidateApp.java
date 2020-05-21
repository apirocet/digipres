package org.apirocet.digipres;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import picocli.CommandLine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
/**
 * This application reads a simple metadata definition file, then validates a metadata file against it.
 * - See README.md for usage details
 *
 * @author Scott Prater
 * @since 2020-04-05
 */
@CommandLine.Command(name = "mdvalidate", mixinStandardHelpOptions = true,
        sortOptions = false,
        version = "Metadata Validator 1.0",
        description = "Command line Metadata Validator 1.0" )
public class MDValidateApp implements Callable<Integer> {
    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @CommandLine.Option(names = {"-l", "--logfile"}, description = "path to log file (default is mdvalidate.log in current directory)")
    String logfile = "mdvalidate.log";

    @CommandLine.Parameters(index = "0", description = "The metadata validator definition file.", paramLabel = "<metadata definition file>")
    private File mddeffile;

    @CommandLine.Parameters(index = "1", description = "The metadata file to validate.", paramLabel = "<metadata file>")
    private File mdfile;

    public static void main( String... args ) {
    int exitCode = new CommandLine(new MDValidateApp()).execute(args);
    System.exit(exitCode);
}

    @Override
    public Integer call() {
        if (logfile != null) {
            System.setProperty("logfile", logfile);
        }

        MetadataDefinitionReader mdreader = new MetadataDefinitionReader(mddeffile);
        List mddef_list = mdreader.loadMetadataDefinition();

        YamlReader ymlreader = null;
        try {
            ymlreader = new YamlReader(new FileReader(mdfile));
        } catch (FileNotFoundException e) {
           System.err.println("Cannot locate yaml file: " + e.getMessage());
           return 1;
        }

        Object object = null;
        try {
            object = ymlreader.read();
        } catch (YamlException e) {
            System.err.println("Cannot parse yaml file: " + e.getMessage());
        }

        Map metadata = (Map)object;

        MetadataValidator mdvalidator = new MetadataValidator(mddef_list, metadata, mdfile);
        return mdvalidator.validate() ? 0 : 1;
    }
}
