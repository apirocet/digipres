package org.apirocet.digipres;

import org.apirocet.digipres.metadata.MetadataModel;
import org.apirocet.digipres.pcms.PCMSClient;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

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

    public static void main( String... args ) {
        int exitCode = new CommandLine(new MDGenerateApp()).execute(args);
        System.exit(exitCode);
    }
    @Override
    public Integer call() {
        if (logfile != null) {
            System.setProperty("logfile", logfile);
        }

        PCMSClient pcms_client = PCMSClient.getInstance();
        pcms_client.getEpisodeTitle(153150);

        SpreadsheetReader sheetreader = new SpreadsheetReader(xlsfile, sheet, program);

        MetadataModel metadata = sheetreader.getMetadata();
        //System.out.println(metadata.toString());

        return 1;
    }
}
