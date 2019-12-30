package org.apirocet.digipres;

import gov.loc.repository.bagit.hash.StandardSupportedAlgorithms;
import org.apirocet.digipres.model.BagManager;
import picocli.CommandLine;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "write", description = "write/update the bag")
public class CmdBagManagerWriter implements Callable<Integer> {

    @CommandLine.ParentCommand
    BagManagerApp bma;

    @CommandLine.Parameters(paramLabel = "<source directory>", description = "the path to the source directory")
    File srcdir;

    @CommandLine.Option(names = {"-a", "--algorithm"},
            defaultValue = "MD5",
            description = "checksum algorithm to use. Supported algorithms are MD5, SHA1, SHA224, SHA256, and SHA512.  Default is MD5.")
    StandardSupportedAlgorithms algorithm;

    @CommandLine.Option(names = {"-m", "--metadata-file"},
            description = "path to optional supplementary bag-info.txt metadata file.")
    File metadataFile;

    @CommandLine.Option(names = "--metadata-fields", split = ",",
            description ="additional bag-info.txt metadata fields, expressed as key=value pairs, separated by a comma.  Example:  --metadata-fields External-Identifier=MyID-0001,Internal-Sender-Identifier=/path/to/MID-0001" )
    Map<String, String> metadataFields;

    @CommandLine.ArgGroup(exclusive = false)
    ElsewhereOptions eogroup;
    static class ElsewhereOptions {
        @CommandLine.Option(names = {"-o", "--outdir"},
                required = true,
                description = "the path to the output directory where the bag will be created.  Optional:  if not specified, the bag will be created in-place at the source directory")
        File outdir;

        @CommandLine.Option(names = {"-r", "--replace"},
                description = "replace the bag contents in the output bag directory")
        boolean replace;
    }

    @Override
    public Integer call() {
        if (bma.logfile != null) {
            System.setProperty("logfile", bma.logfile);
        }
        
        BagManagerWriter bw = new BagManagerWriter(createBagManagerDTO());
        return bw.write();
    }
    
    private BagManager createBagManagerDTO() {
        BagManager bm = new BagManager();

        if (eogroup != null) {
            bm.setBagdir(eogroup.outdir);
            bm.setSrcdir(srcdir);
            bm.setReplace(eogroup.replace);
        } else {
            bm.setBagdir(srcdir);
        }

        if (metadataFile != null) {
            bm.setMetadataFile(metadataFile);
        }

        if (metadataFields != null && ! metadataFields.isEmpty()) {
            bm.setMetadataFields(metadataFields);
        }

        bm.setAlgorithm(algorithm);
        return bm;
    }
}
