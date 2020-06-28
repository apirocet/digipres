package org.apirocet.digipres.mapper;

import org.apirocet.digipres.SpreadsheetReader;
import org.apirocet.digipres.model.ArchiveObject;
import org.slf4j.Logger;

import java.util.Date;

import static org.slf4j.LoggerFactory.getLogger;

public class ArchiveObjectMapper {


        private static final Logger LOGGER = getLogger(org.apirocet.digipres.mapper.ArchiveObjectMapper.class);

        public ArchiveObject mapRowToArchiveObject(int magazine_pcms_id) {
            ArchiveObject archive_object = new ArchiveObject();
            archive_object.setProgram(SpreadsheetReader.getProgram());
            archive_object.setMagazinePcmsId(magazine_pcms_id);
            archive_object.setDateArchiveUpdated(new Date());

            return archive_object;
        }

}
