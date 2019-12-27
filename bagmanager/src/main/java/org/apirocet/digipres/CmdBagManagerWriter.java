package org.apirocet.digipres;

import gov.loc.repository.bagit.hash.StandardSupportedAlgorithms;
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

    @CommandLine.Option(names = {"-a", "--algorithm"}, defaultValue = "MD5", description = "checksum algorithm to use. Supported algorithms are MD5, SHA1, SHA224, SHA256, and SHA512.  Default is MD5.")
    StandardSupportedAlgorithms algorithm;


    @Override
    public Integer call() throws IOException, NoSuchAlgorithmException {
        if (bm.logfile != null) {
            System.setProperty("logfile", bm.logfile);
        }
        BagManagerWriter bw = new BagManagerWriter(bagdir, algorithm);
        return bw.write();
    }
}
