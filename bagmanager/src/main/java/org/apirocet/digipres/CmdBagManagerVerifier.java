package org.apirocet.digipres;

import org.apirocet.digipres.model.BagManager;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "verify", description = "verify the bag")
public class CmdBagManagerVerifier implements Callable<Integer> {

    @CommandLine.ParentCommand
    private BagManagerApp bma;

    @CommandLine.Parameters(paramLabel = "<bag directory>", description = "the path to the bag directory")
    File bagdir;

    @CommandLine.Option(names = {"--with-profile"},
            description = "verify conformance to BagIt profile")
    boolean verifyWithProfile;

    @Override
    public Integer call () {
        if (bma.logfile != null) {
            System.setProperty("logfile", bma.logfile);
        }
        BagManagerVerifier bmv = new BagManagerVerifier(createBagManagerDTO());
        return bmv.verify() ? 0 : 1;
    }

    private BagManager createBagManagerDTO() {
        BagManager bm = new BagManager();
        bm.setBagdir(bagdir);

        bm.setVerifyWithProfile(verifyWithProfile);

        return bm;
    }
}
