package org.apirocet.digipres;

import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "write", description = "write/update the bag")
public class CmdBagManagerWriter implements Callable<Integer> {

    @CommandLine.ParentCommand
    private BagManager bm;

    @CommandLine.Parameters(paramLabel = "<bag directory>", description = "the path to the bag directory")
    File bagdir;

    @Override
    public Integer call() throws IOException, NoSuchAlgorithmException {
        if (bm.logfile != null) {
            System.setProperty("logfile", bm.logfile);
        }

        BagManagerWriter bw = new BagManagerWriter(bagdir);
        return bw.write();
    }
}
