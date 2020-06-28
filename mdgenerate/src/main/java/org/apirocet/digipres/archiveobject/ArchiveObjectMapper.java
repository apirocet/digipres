package org.apirocet.digipres.archiveobject;

import org.apirocet.digipres.SpreadsheetReader;
import org.slf4j.Logger;

import java.util.Date;

import static org.slf4j.LoggerFactory.getLogger;

public class ArchiveObjectMapper {


        private static final Logger LOGGER = getLogger(ArchiveObjectMapper.class);

        public ArchiveObjectModel mapRowToArchiveObject(int magazine_pcms_id) {
            ArchiveObjectModel archive_object = new ArchiveObjectModel();
            archive_object.setProgram(SpreadsheetReader.getProgram());
            archive_object.setMagazinePcmsId(magazine_pcms_id);
            archive_object.setDateArchiveUpdated(new Date());

            return archive_object;
        }

}
