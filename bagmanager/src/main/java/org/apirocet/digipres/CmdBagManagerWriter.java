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
    private BagManagerApp bm;

    @CommandLine.Parameters(paramLabel = "<source directory>", description = "the path to the source directory")
    File bagdir;

    @CommandLine.Option(names = {"-a", "--algorithm"},
            defaultValue = "MD5",
            description = "checksum algorithm to use. Supported algorithms are MD5, SHA1, SHA224, SHA256, and SHA512.  Default is MD5.")
    StandardSupportedAlgorithms algorithm;

    @CommandLine.Option(names = {"-m", "--metadata-file"},
            description = "path to supplementary bag-info.txt metadata file.")
    File metadataFile;

    @CommandLine.ArgGroup(exclusive = false)
    ElsewhereOptions eogroup;
    static class ElsewhereOptions {
        @CommandLine.Option(names = {"-o", "--outdir"},
                required = true,
                description = "the path to the output directory where the bag will be created.  Optional:  if not specified, the bag will be created in-place at the source directory")
        File outbagdir;

        @CommandLine.Option(names = {"-r", "--replace"},
                required = false,
                description = "replace the bag contents in the output bag directory")
        boolean replace;
    }

    @Override
    public Integer call() throws IOException, NoSuchAlgorithmException {
        if (bm.logfile != null) {
            System.setProperty("logfile", bm.logfile);
        }

        BagManagerWriter bw = null;

        if (eogroup != null) {
            bw = new BagManagerWriter(bagdir, eogroup.outbagdir, algorithm, eogroup.replace);
        } else {
            bw = new BagManagerWriter(bagdir, algorithm);
        }

        return bw.write();
    }
}
