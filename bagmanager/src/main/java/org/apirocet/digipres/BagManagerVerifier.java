package org.apirocet.digipres;

import gov.loc.repository.bagit.conformance.BagProfileChecker;
import gov.loc.repository.bagit.domain.Bag;
import gov.loc.repository.bagit.exceptions.InvalidBagMetadataException;
import gov.loc.repository.bagit.reader.BagReader;
import gov.loc.repository.bagit.verify.BagVerifier;
import org.apirocet.digipres.model.BagManager;
import org.slf4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

import static org.slf4j.LoggerFactory.getLogger;

public class BagManagerVerifier {

    private static final Logger LOGGER = getLogger(BagManagerWriter.class);

    private static final boolean includeHiddenFiles = false;

    private File bagdir;
    private boolean verifyWithProfile;

    public BagManagerVerifier(BagManager bm) {
        this.bagdir = bm.getBagdir();
        this.verifyWithProfile = bm.isVerifiedWithProfile();
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

            if (verifyWithProfile) {
                checkWithProfile(bag);
            }
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

    private void checkWithProfile(Bag bag) throws Exception {

        if (bag.getMetadata().get("BagIt-Profile-Identifier") == null ||
                bag.getMetadata().get("BagIt-Profile-Identifier").isEmpty()) {
            LOGGER.warn("Bag does not have a profile identifier.  Skipping profile conformance check.");
            return;
        }

        String profileUrlString = bag.getMetadata().get("BagIt-Profile-Identifier").get(0);
        if (profileUrlString == null || profileUrlString.isBlank()) {
            throw new InvalidBagMetadataException("Profile identifier is blank.  Bag is not valid.");
        }

        URL profileUrl;
        try {
            // Not foolproof, but close enough
            profileUrl = new URL(profileUrlString);
            profileUrl.toURI();
        } catch (MalformedURLException| URISyntaxException ex) {
            throw new InvalidBagMetadataException("BagIt profile identifier is not a valid URL.  Bag is not valid.");
        }

        LOGGER.info("Verifying conformance to BagIt profile", bagdir);

        InputStream profile = profileUrl.openStream();
        BagProfileChecker.bagConformsToProfile(profile, bag);

        LOGGER.info("Bag conforms to profile '{}'", profileUrlString);
    }
}
