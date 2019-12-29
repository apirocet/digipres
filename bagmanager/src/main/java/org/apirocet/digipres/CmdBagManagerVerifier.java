package org.apirocet.digipres;

import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "verify", description = "verify the bag")
public class CmdBagManagerVerifier implements Callable<Integer> {

    @CommandLine.ParentCommand
    private BagManagerApp bm;

    @CommandLine.Parameters(paramLabel = "<bag directory>", description = "the path to the bag directory")
    File bagdir;

    @Override
    public Integer call () {
        if (bm.logfile != null) {
            System.setProperty("logfile", bm.logfile);
        }

        System.out.println("Verifying bag " + bagdir);
        return 0;
    }
}
