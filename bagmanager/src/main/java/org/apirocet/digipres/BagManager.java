package org.apirocet.digipres;

import org.apirocet.digipres.BagWriter;
import org.apirocet.digipres.BagVerifier;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
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
        version = "BagIt Bag Manager - 1.0",
        description = "Command line BagIt bag manager",
        subcommands = { BagWriter.class, BagVerifier.class })
public class BagManager implements Callable<Integer> {
    public static void main( String... args ) {
        int exitCode = new CommandLine(new BagManager()).execute(args);
        System.exit(exitCode);
    }

    @Parameters(arity = "1", index = "0", description = "The bag directory")
    File bagdir;

    @Override
    public Integer call() throws Exception {
        return 0;
    }
}
