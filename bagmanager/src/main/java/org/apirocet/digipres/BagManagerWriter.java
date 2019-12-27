package org.apirocet.digipres;

import gov.loc.repository.bagit.creator.BagCreator;
import gov.loc.repository.bagit.domain.Bag;
import gov.loc.repository.bagit.hash.StandardSupportedAlgorithms;
import gov.loc.repository.bagit.reader.BagReader;
import gov.loc.repository.bagit.verify.BagVerifier;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;

import static org.slf4j.LoggerFactory.getLogger;

public class BagManagerWriter {

    private static final Logger LOGGER = getLogger(BagManagerWriter.class);

    private File bagdir;
    private File outbagdir;
    private Bag bag;
    private boolean includeHiddenFiles = false;
    private boolean replace = false;
    private StandardSupportedAlgorithms algorithm;

    public BagManagerWriter(File bagdir, StandardSupportedAlgorithms algorithm) {
        this.bagdir = bagdir;
        this.algorithm = algorithm;
    }

    public BagManagerWriter(File bagdir, File outbagdir, StandardSupportedAlgorithms algorithm, boolean replace) {
        this.bagdir = bagdir;
        this.outbagdir = outbagdir;
        this.algorithm = algorithm;
        this.replace = replace;
    }

    public Integer write() {
        if (outbagdir == null) {
            return writeInPlace(Paths.get(bagdir.getAbsolutePath())) && verify(bagdir) ? 0 : 1;
        } else {
            return writeElsewhere(Paths.get(bagdir.getAbsolutePath()), Paths.get(outbagdir.getAbsolutePath())) && verify(outbagdir) ? 0 : 1;
        }
    }

    private boolean writeInPlace(Path folder) {
        if (Files.notExists(folder)) {
            LOGGER.error("Cannot create in-place bag at '{}': directory does not exist", folder);
            return false;
        }

        if (isBag(folder)) {
            LOGGER.error("Cannot create in-place bag at '{}': bag already exists at this location", folder);
            return false;
        }

        LOGGER.info("Creating bag in place from contents at '{}' with {} checksums", folder.toAbsolutePath(), algorithm.getMessageDigestName());

        try {
            bag = BagCreator.bagInPlace(folder, Collections.singletonList(algorithm), includeHiddenFiles);
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.error("Cannot create bag with checksum algorithm {}: {}", algorithm.getMessageDigestName(), ex.getMessage());
            return false;
        } catch (IOException ex) {
            LOGGER.error("Cannot create in-place bag at '{}': {}", folder, ex.getMessage());
            return false;
        }

        LOGGER.info("Bag created at '{}'", folder.toAbsolutePath());

        return true;
    }

    private boolean writeElsewhere(Path srcfolder, Path outfolder) {
        boolean removeExistingBag = false;

        Path absSrc = srcfolder.toAbsolutePath().normalize();
        Path absDest = outfolder.toAbsolutePath().normalize();
        if (absSrc.toString().equals(absDest.toString())) {
            LOGGER.error("Cannot create bag:  source and destination are the same", outfolder);
            return false;
        }

        if (Files.exists(outfolder)) {
            if (isBag(outfolder)) {
                if (!replace) {
                    LOGGER.error("Cannot create bag at '{}': bag already exists at this location.  Please use the '--replace' flag to replace the contents of this bag", outfolder);
                    return false;
                } else {
                    removeExistingBag = true;
                }
            } else {
                LOGGER.error("Cannot create bag at '{}': directory exists", outfolder);
                return false;
            }
        }

        if (removeExistingBag) {
            LOGGER.info("Removing existing bag at '{}'", outfolder);
            Path delPath = outfolder.resolve(outfolder);

            try {
                Files.walk(delPath).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            } catch (IOException ex) {
                LOGGER.error("Cannot clean bag destination", ex);
                return false;
            }
        }

        try {
            copyFolder(srcfolder.toFile(), outfolder.toFile());
        } catch (IOException ex) {
            LOGGER.error("Cannot copy source contents to output bag folder", ex);
            return false;
        }

        return writeInPlace(outfolder);
    }

    private boolean verify(File bagdir) {
        BagVerifier bv = new BagVerifier();
        BagReader reader = new BagReader();
        LOGGER.info("Verifying valid bag from contents at '{}'", bagdir.getAbsolutePath());
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

        return true;
    }

    private boolean isBag(Path folder) {
        String bagitfile = folder.toString() + File.separatorChar + "bagit.txt";
        Path bag = folder.resolve(bagitfile);
        return Files.exists(bag);
    }

    private void copyFolder(File srcfolder, File destfolder) throws IOException
    {
        if (srcfolder.isDirectory())
        {
            if (!destfolder.exists())
            {
                destfolder.mkdir();
            }

            String files[] = srcfolder.list();
            for (String file : files)
            {
                File srcFile = new File(srcfolder, file);
                File destFile = new File(destfolder, file);

                copyFolder(srcFile, destFile);
            }
        }
        else
        {
            Files.copy(srcfolder.toPath(), destfolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}

