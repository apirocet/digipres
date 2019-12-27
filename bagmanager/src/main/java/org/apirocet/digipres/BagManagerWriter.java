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
    private StandardSupportedAlgorithms algorithm;

    public BagManagerWriter(File bagdir, StandardSupportedAlgorithms algorithm) {
        this.bagdir = bagdir;
        this.algorithm = algorithm;
    }

    public Integer write() {
        if (outbagdir == null) {
            return writeInPlace();
        } else {
            return writeElsewhere();
        }
    }

    private Integer writeElsewhere() {
        return 0;
    }

    private Integer writeInPlace() {
        Path folder = Paths.get(bagdir.getAbsolutePath());

        if (Files.notExists(folder)) {
            System.out.println("Cannot create in-place bag at '" + bagdir +"': directory does not exist");
            LOGGER.error("Cannot create in-place bag at '{}': directory does not exist", bagdir);
            return 1;
        }

        if (isBag(folder)) {
            System.out.println("Cannot create in-place bag at '" + bagdir +"': bag already exists at this location");
            LOGGER.error("Cannot create in-place bag at '{}': bag already exists at this location", bagdir);
            return 1;
        }

        System.out.println("Creating bag in place from contents at '" + bagdir.getAbsolutePath() + "' with " + algorithm.getMessageDigestName() + " checksums");
        LOGGER.info("Creating bag in place from contents at '{}' with '{}' checksums", bagdir.getAbsolutePath(), algorithm.getMessageDigestName());

        boolean includeHiddenFiles = false;
        Bag bag = null;
        try {
            bag = BagCreator.bagInPlace(folder, Arrays.asList(algorithm), includeHiddenFiles);
        } catch (NoSuchAlgorithmException ex) {
            System.err.println("Cannot create bag with checksum algorithm '" + algorithm.getMessageDigestName() +"': " + ex.getMessage());
            LOGGER.error("Cannot create bag with checksum algorithm '{}': {}", algorithm.getMessageDigestName(), ex.getMessage());
            return 1;
        } catch (IOException ex) {
            System.err.println("Cannot create in-place bag at '" + folder +"': " + ex.getMessage());
            LOGGER.error("Cannot create in-place bag at '{}': {}", folder, ex.getMessage());
            return 1;
        }

        System.out.println("Bag created at '" + bagdir.getAbsolutePath() + "'");
        LOGGER.info("Bag created at '{}'", bagdir.getAbsolutePath());

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
        String bagitfile = folder.toString() + File.separatorChar + "bagit.txt";
        Path bag = folder.resolve(bagitfile);
        System.out.println(bag);
        return Files.exists(bag);
    }
}
