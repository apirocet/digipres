package org.apirocet.digipres;

import gov.loc.repository.bagit.domain.Metadata;
import gov.loc.repository.bagit.util.PathUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

import static org.slf4j.LoggerFactory.getLogger;

public class MetadataManager {

    private static final Logger LOGGER = getLogger(MetadataManager.class);

    private Properties metadataProperties = new Properties();
    private Metadata metadata = new Metadata();

    public MetadataManager() { }

    public Metadata setMetadata(File bagdir, File mdproperties) throws IOException {
        try (final FileChannel channel = FileChannel.open(mdproperties.toPath(), StandardOpenOption.READ);
             final FileLock lock = channel.lock(0L, Long.MAX_VALUE, true)) {
            metadataProperties.load(Channels.newInputStream(channel));
        }
        metadataProperties.forEach((key, value) -> metadata.add((String) key, (String) value));

        return setMetadata(bagdir);
    }

    public Metadata setMetadata(File bagdir) throws IOException {
        String size = getBagSize(bagdir);
        metadata.add("Bag-Size", size);
        return metadata;
    }

    private String getBagSize(File bagdir) throws IOException {
        String size = PathUtils.generatePayloadOxum(bagdir.toPath());
        String bytes = size.split("\\.")[0];
        return bytesToHumans(bytes);
    }

    private String bytesToHumans(String size) {
        long bytes = Long.parseLong(size);
        long b = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        return b < 1000L ? bytes + " B"
                : b < 999_950L ? String.format("%.1f KB", b / 1e3)
                : (b /= 1000) < 999_950L ? String.format("%.1f MB", b / 1e3)
                : (b /= 1000) < 999_950L ? String.format("%.1f GB", b / 1e3)
                : (b /= 1000) < 999_950L ? String.format("%.1f TB", b / 1e3)
                : (b /= 1000) < 999_950L ? String.format("%.1f PB", b / 1e3)
                : String.format("%.1f EB", b / 1e6);
    }
}
