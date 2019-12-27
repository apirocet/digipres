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
    private Bag bag;
    private boolean includeHiddenFiles = false;
    private StandardSupportedAlgorithms algorithm;

    public BagManagerWriter(File bagdir, StandardSupportedAlgorithms algorithm) {
        this.bagdir = bagdir;
        this.algorithm = algorithm;
    }

    public Integer write() {
        if (outbagdir == null) {
            return writeInPlace() && verify() ? 0 : 1;
        } else {
            return writeElsewhere();
        }
    }

    private Integer writeElsewhere() {
        return 0;
    }

    private Boolean writeInPlace() {
        Path folder = Paths.get(bagdir.getAbsolutePath());

        if (Files.notExists(folder)) {
            System.out.println("Cannot create in-place bag at '" + bagdir +"': directory does not exist");
            LOGGER.error("Cannot create in-place bag at '{}': directory does not exist", bagdir);
            return false;
        }

        if (isBag(folder)) {
            System.out.println("Cannot create in-place bag at '" + bagdir +"': bag already exists at this location");
            LOGGER.error("Cannot create in-place bag at '{}': bag already exists at this location", bagdir);
            return false;
        }

        System.out.println("Creating bag in place from contents at '" + bagdir.getAbsolutePath() + "' with " + algorithm.getMessageDigestName() + " checksums");
        LOGGER.info("Creating bag in place from contents at '{}' with '{}' checksums", bagdir.getAbsolutePath(), algorithm.getMessageDigestName());

        try {
            bag = BagCreator.bagInPlace(folder, Arrays.asList(algorithm), includeHiddenFiles);
        } catch (NoSuchAlgorithmException ex) {
            System.err.println("Cannot create bag with checksum algorithm '" + algorithm.getMessageDigestName() +"': " + ex.getMessage());
            LOGGER.error("Cannot create bag with checksum algorithm '{}': {}", algorithm.getMessageDigestName(), ex.getMessage());
            return false;
        } catch (IOException ex) {
            System.err.println("Cannot create in-place bag at '" + folder +"': " + ex.getMessage());
            LOGGER.error("Cannot create in-place bag at '{}': {}", folder, ex.getMessage());
            return false;
        }

        System.out.println("Bag created at '" + bagdir.getAbsolutePath() + "'");
        LOGGER.info("Bag created at '{}'", bagdir.getAbsolutePath());
        
        return true;
    }

    private Boolean verify() {
        BagVerifier bv = new BagVerifier();
        LOGGER.info("Verifying valid bag from contents at '{}'", bagdir.getAbsolutePath());
        try {
            bv.isValid(bag, includeHiddenFiles);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return false;
        }

        return true;
    }

    private Boolean isBag(Path folder) {
        String bagitfile = folder.toString() + File.separatorChar + "bagit.txt";
        Path bag = folder.resolve(bagitfile);
        System.out.println(bag);
        return Files.exists(bag);
    }
}
