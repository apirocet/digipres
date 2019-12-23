package org.apirocet.digipres;

import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "verify", description = "verify the bag")
public class CmdBagVerifier implements Callable<Integer> {

    @CommandLine.Parameters(paramLabel = "<bag directory>", description = "the path to the bag directory")
    File bagdir;

    @Override
    public Integer call () {
        System.out.println("Verifying bag " + bagdir);
        return 0;
    }
}
