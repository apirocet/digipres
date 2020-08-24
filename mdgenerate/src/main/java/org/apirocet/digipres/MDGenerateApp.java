package org.apirocet.digipres;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.apirocet.digipres.metadata.MetadataModel;
import org.apirocet.digipres.pcms.PCMSClient;
import org.apirocet.digipres.pcms.PCMSDataMapper;
import org.slf4j.Logger;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * This application generates a metadata file from an Excel spreadsheet.
 * - See README.md for usage details
 *
 * @author Scott Prater
 * @since 2020-06-05
 */
@CommandLine.Command(name = "mdgenerate", mixinStandardHelpOptions = true,
        sortOptions = false,
        version = "Metadata Generator 1.0",
        description = "Command line Metadata Generator 1.0" )
public class MDGenerateApp implements Callable<Integer> {

    private static final Logger LOGGER = getLogger(MDGenerateApp.class);

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @CommandLine.Option(names = {"-l", "--logfile"}, description = "path to log file (default is mdgenerate.log in current directory)")
    String logfile = "mdgenerate.log";

    @CommandLine.Parameters(index = "0", description = "The spreadsheet file", paramLabel = "<spreadsheet file>")
    private File xlsfile;

    @CommandLine.Parameters(index = "1", description = "The name of the sheet to read in the spreadsheet file", paramLabel = "<sheet>")
    private String sheet;

    @CommandLine.Option(names = {"-p", "--program"}, description = "Podcast program name (default is '${DEFAULT-VALUE}')", defaultValue = "Poetry Magazine")
    private String program;

    @CommandLine.Option(names = {"-d", "--date"}, description = "Podcast episode date to generate metadata for (default is all podcasts in sheet)")
    private String episode_date;

    public static void main( String... args ) {
        int exitCode = new CommandLine(new MDGenerateApp()).execute(args);
        System.exit(exitCode);
    }
    @Override
    public Integer call() {
        if (logfile != null) {
            System.setProperty("logfile", logfile);
        }

        SpreadsheetReader sheetreader = new SpreadsheetReader(xlsfile, sheet, program, episode_date);

        MetadataModel metadata = sheetreader.getMetadata();
        YAMLWriter yw =YAMLWriter.getInstance();
        yw.writeYAML(metadata);

        return 0;
    }
}
