package org.apirocet.digipres.archiveobject;

import org.apirocet.digipres.SpreadsheetReader;
import org.apirocet.digipres.pcms.PCMSDataMapper;
import org.slf4j.Logger;

import java.util.Date;
import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

public class ArchiveObjectMapper {

        private static final PCMSDataMapper pcms = new PCMSDataMapper();
        private static final Logger LOGGER = getLogger(ArchiveObjectMapper.class);

        public ArchiveObjectModel mapRowToArchiveObject(int magazine_pcms_id) {
            ArchiveObjectModel archive_object = new ArchiveObjectModel();
            archive_object.setArchiveId(UUID.randomUUID().toString());
            archive_object.setProgram(SpreadsheetReader.getProgram());
            archive_object.setMagazinePcmsId(magazine_pcms_id);
            archive_object.addDatesArchiveUpdated(new Date());

            archive_object.setMagazineDate(pcms.getMagazineDate(magazine_pcms_id));

            return archive_object;
        }

}
