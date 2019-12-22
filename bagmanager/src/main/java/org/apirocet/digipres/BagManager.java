package org.apirocet.digipres;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

/**
 * This class provides a simple CLI for writing, reading and verifying BagIt bags.
 * - See README.md for usage details
 * https://tools.ietf.org/html/rfc8493
 *
 * @author Scott Prater
 * @since 2019-12-20
 */

@Command(name = "bagmanager", mixinStandardHelpOptions = true,
        sortOptions = false,
        version = "BagIt Bag Manager 1.0",
        description = "Command line BagIt bag manager",
        synopsisSubcommandLabel = "( write | verify )",
        subcommands = { BagWriter.class, BagVerifier.class })
public class BagManager implements Callable<Integer> {

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    public static void main( String... args ) {
        int exitCode = new CommandLine(new BagManager()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        // if the command was invoked without subcommand, show the usage help
        spec.commandLine().usage(System.err);
        return 1;
    }
}
