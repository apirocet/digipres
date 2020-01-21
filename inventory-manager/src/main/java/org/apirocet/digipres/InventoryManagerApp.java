package org.apirocet.digipres;

import picocli.CommandLine;

import java.util.concurrent.Callable;

/*
* @author Scott Prater
* @since 2020-01-21
 */
@CommandLine.Command(name = "bagmanager", mixinStandardHelpOptions = true,
        sortOptions = false,
        version = "Inventory Spreadsheet Manager 1.0",
        description = "Command line inventory spreadsheet manager",
        synopsisSubcommandLabel = "( read )",
        subcommands = { CmdInventoryManagerReader.class } )
public class InventoryManagerApp implements Callable<Integer> {

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @CommandLine.Option(names = {"-l", "--logfile"}, description = "path to log file (default is inventorymanager.log in current directory)")
    String logfile;

    public static void main( String... args ) {
        int exitCode = new CommandLine(new InventoryManagerApp()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        spec.commandLine().usage(System.err);
        return null;
    }
}
