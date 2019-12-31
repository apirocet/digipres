package org.apirocet.digipres;

import com.github.jscancella.domain.BagBuilder;
import com.github.jscancella.domain.Version;
import com.github.jscancella.hash.StandardHasher;
import org.apirocet.digipres.model.BagManager;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class BagManagerWriter {

    private static final Logger LOGGER = getLogger(BagManagerWriter.class);

    private BagManager bm;

    private File bagdir;
    private File srcdir;
    private boolean replace;
    private StandardHasher algorithm;
    private File metadataFile;

    public BagManagerWriter(BagManager bm) {
        this.bm = bm;
        this.bagdir = bm.getBagdir();
        this.srcdir = bm.getSrcdir();
        this.algorithm = bm.getAlgorithm();
        this.replace = bm.isReplace();
        this.metadataFile = bm.getMetadataFile();
    }

    public Integer write() {

        if (! checkFiles())
            return 1;

        if (metadataFile != null && ! Files.isReadable(metadataFile.toPath())) {
            LOGGER.error("Metadata file '{}' is not readable", metadataFile);
            return 1;
        }

        BagManagerVerifier bmv = new BagManagerVerifier(bm);
        if (srcdir == null) {
            return writeInPlace(Paths.get(bagdir.getAbsolutePath())) && bmv.verify() ? 0 : 1;
        } else {
            return writeElsewhere(Paths.get(srcdir.getAbsolutePath()), Paths.get(bagdir.getAbsolutePath())) && bmv.verify() ? 0 : 1;
        }
    }

    private boolean checkFiles() {
        Path folder = bagdir.toPath();

        if (srcdir == null) {
            if (! Files.isWritable(folder)) {
                LOGGER.error("Cannot create in-place bag at '{}': directory does not exist or is not writable", folder);
                return false;
            }

            if (isBag(folder)) {
                LOGGER.error("Cannot create in-place bag at '{}': bag already exists at this location", folder);
                return false;
            }
        } else {
            Path srcFolder = srcdir.toPath();

            if (! Files.isReadable(srcFolder)) {
                LOGGER.error("Cannot create bag from contents at '{}': directory does not exist or is not readable", srcFolder);
                return false;
            }

            Path absSrc = srcFolder.toAbsolutePath().normalize();
            Path absDest = folder.toAbsolutePath().normalize();
            if (absSrc.toString().equals(absDest.toString())) {
                LOGGER.error("Cannot create bag: source and destination are the same");
                return false;
            }

            if (Files.exists(folder)) {
                if (isBag(folder)) {
                    if (!replace) {
                        LOGGER.error("Cannot create bag at '{}': bag already exists at this location.  Please use the '--replace' flag to replace the contents of this bag", folder);
                        return false;
                    }
                } else {
                    LOGGER.error("Cannot create bag at '{}': directory exists", folder);
                    return false;
                }
            }
        }

        return true;
    }

    private boolean writeInPlace(Path folder) {
        LOGGER.info("Creating bag in place from contents at '{}' with {} checksums", folder.toAbsolutePath(), algorithm.getBagitAlgorithmName());

        try {
            MetadataManager mdm = new MetadataManager();
            Map<String,String> md = mdm.setMetadata(bm);
            BagBuilder bb = new BagBuilder();
            bb.version(Version.VERSION_1_0());
            bb.addAlgorithm(algorithm.getBagitAlgorithmName());
            md.forEach(bb::addMetadata);
            bb.bagLocation(folder);
            bb.write();
        } catch (IOException ex) {
            LOGGER.error("Cannot create in-place bag:", ex);
            return false;
        }

        LOGGER.info("Bag created at '{}'", folder.toAbsolutePath());

        return true;
    }

    private boolean writeElsewhere(Path srcfolder, Path outfolder) {

        if (replace && Files.exists(outfolder)) {
            if (Files.isWritable(outfolder)) {
                LOGGER.info("Removing existing bag at '{}'", outfolder);
                Path delPath = outfolder.resolve(outfolder);

                try {
                    Files.walk(delPath).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                } catch (IOException ex) {
                    LOGGER.error("Cannot clean bag destination", ex);
                    return false;
                }
            } else {
                LOGGER.error("Cannot clean bag destination:  directory is not writable");
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

            String[] files = srcfolder.list();
            if (files != null && files.length != 0) {
                for (String file : files) {
                    File srcFile = new File(srcfolder, file);
                    File destFile = new File(destfolder, file);

                    copyFolder(srcFile, destFile);
                }
            }
        }
        else
        {
            Files.copy(srcfolder.toPath(), destfolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}

