package org.apirocet.digipres;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.apirocet.digipres.archiveobject.ArchiveObjectModel;
import org.apirocet.digipres.metadata.MetadataModel;
import org.apirocet.digipres.pcms.PCMSClient;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import static org.slf4j.LoggerFactory.getLogger;

public final class YAMLWriter {
    private static final Logger LOGGER = getLogger(YAMLWriter.class);
    private static final SimpleDateFormat yrmonth = new SimpleDateFormat("yyyy-MM");
    private static volatile YAMLWriter instance;
    private static ObjectMapper mapper;

    private YAMLWriter() {
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static YAMLWriter getInstance() {
        if (instance == null) {
            synchronized (YAMLWriter.class) {
                if (instance == null) {
                    instance = new YAMLWriter();
                    initializeMapper();
                }
            }
        }
        return instance;
    }

    public void writeYAML(MetadataModel metadata) {
        for (ArchiveObjectModel ao : metadata.getArchiveObjects()) {
            String mag_date = yrmonth.format(ao.getMagazineDate());
            File yaml_file = new File("metadata-" + mag_date + ".yml");
            try {
                mapper.writeValue(yaml_file, ao);
            } catch (
                    IOException ex) {
                LOGGER.error("Cannot create metadata file: {}", ex.getMessage());
                System.exit(1);
            }
        }
    }

    private static void initializeMapper() {
        mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
}
