package org.apirocet.digipres;

import gov.loc.repository.bagit.creator.BagCreator;
import gov.loc.repository.bagit.domain.Bag;
import gov.loc.repository.bagit.hash.StandardSupportedAlgorithms;
import gov.loc.repository.bagit.verify.BagVerifier;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.slf4j.LoggerFactory.getLogger;

public class BagManagerWriter {

    private static final Logger LOGGER = getLogger(BagManagerWriter.class);

    private File bagdir;
    private File outbagdir;

    public BagManagerWriter(File bagdir) {
        this.bagdir = bagdir;
    }

    public Integer write() throws IOException, NoSuchAlgorithmException {
        if (outbagdir == null) {
            return writeInPlace();
        } else {
            return writeElsewhere();
        }
    }

    private Integer writeElsewhere() {
        return 0;
    }

    private Integer writeInPlace() throws NoSuchAlgorithmException, IOException {
        Path folder = Paths.get(bagdir.getAbsolutePath());

        if (Files.notExists(folder)) {
            LOGGER.error("Cannot create in-place bag at '{}': directory does not exist", bagdir);
            return 1;
        }

        if (isBag(folder)) {
            LOGGER.error("Cannot create in-place bag at '{}': bag already exists at this location.", bagdir);
            return 1;
        }

        LOGGER.info("Creating bag in place from contents at '{}'", bagdir.getAbsolutePath());

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

    private Boolean isBag(Path folder) {
        String bagitfile = File.separatorChar + "bagit.txt";
        Path bag = folder.resolve(bagitfile);
        System.out.println(bag);
        return Files.exists(bag);
    }
}
