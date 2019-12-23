package org.apirocet.digipres;

import gov.loc.repository.bagit.creator.BagCreator;
import gov.loc.repository.bagit.domain.Bag;
import gov.loc.repository.bagit.hash.StandardSupportedAlgorithms;
import gov.loc.repository.bagit.verify.BagVerifier;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.slf4j.LoggerFactory.getLogger;

public class BagManagerWriter {

    private static final Logger LOGGER = getLogger(BagManagerWriter.class);

    private File bagdir;

    public BagManagerWriter(File bagdir) {
        this.bagdir = bagdir;
    }

    public Integer write() throws IOException, NoSuchAlgorithmException {

        // TODO:  writes bag info in-place!
        LOGGER.info("Creating bag from contents at '{}'", bagdir.getAbsolutePath());
        Path folder = Paths.get(bagdir.getAbsolutePath());
        StandardSupportedAlgorithms algorithm = StandardSupportedAlgorithms.SHA1;
        boolean includeHiddenFiles = false;
        Bag bag = BagCreator.bagInPlace(folder, Arrays.asList(algorithm), includeHiddenFiles);

        LOGGER.info("Verifying complete bag from contents at '{}'", bagdir.getAbsolutePath());
        BagVerifier bv = new BagVerifier();
        try {
            bv.isComplete(bag, includeHiddenFiles);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }

        LOGGER.info("Verifying valid bag from contents at '{}'", bagdir.getAbsolutePath());
        try {
            bv.isValid(bag, includeHiddenFiles);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }

        //LOGGER.info("Writing out bag to '/var/tmp/tt2'");
        //Path outputDir = Paths.get("/var/tmp/tt2");
        //BagWriter.write(bag, outputDir);
        return 0;
    }
}
