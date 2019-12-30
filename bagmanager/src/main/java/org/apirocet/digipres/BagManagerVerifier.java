package org.apirocet.digipres;

import gov.loc.repository.bagit.domain.Bag;
import gov.loc.repository.bagit.reader.BagReader;
import gov.loc.repository.bagit.verify.BagVerifier;
import org.apirocet.digipres.model.BagManager;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

import static org.slf4j.LoggerFactory.getLogger;

public class BagManagerVerifier {

    private static final Logger LOGGER = getLogger(BagManagerWriter.class);

    private static final boolean includeHiddenFiles = false;

    private File bagdir;
    private File profileFile;

    public BagManagerVerifier(BagManager bm) {
        this.bagdir = bm.getBagdir();
        this.profileFile = bm.getProfileFile();
    }

    public boolean verify() {
        BagVerifier bv = new BagVerifier();
        BagReader reader = new BagReader();
        LOGGER.info("Verifying valid bag from contents at '{}'", bagdir);

        if (! Files.isReadable(bagdir.toPath())) {
            LOGGER.error("'{}' is not readable", bagdir);
            return false;
        }
        
        try {
            Bag bag = reader.read(bagdir.toPath());
            bv.isValid(bag, includeHiddenFiles);
        } catch (NoSuchFileException ex) {
            LOGGER.error("'{}' does not appear to be a bag:  missing bagit.txt file", bagdir);
            return false;
        } catch (Exception ex) {
            LOGGER.error("Bag is not valid", ex);
            return false;
        }

        LOGGER.info("Bag at '{}' is valid and complete", bagdir);
        return true;
    }
}
