package org.apirocet.digipres.author;

import org.apache.poi.ss.usermodel.Row;
import org.apirocet.digipres.SpreadsheetReader;
import org.apirocet.digipres.pcms.PCMSDataMapper;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class AuthorMapper {

    private static final Logger LOGGER = getLogger(AuthorMapper.class);
    private static final PCMSDataMapper pcms = new PCMSDataMapper();

    public AuthorModel mapRowToAuthor(Row row, int author_pcms_id) {
        AuthorModel author = new AuthorModel();

        author.setPcmsId(author_pcms_id);
        String rights_file = getAuthorRightsFile(row);

        if (rights_file != null && ! rights_file.isEmpty())
            author.setRightsFile("Rights Data/" + rights_file);

        if (author_pcms_id != 0) {
            author.setName(pcms.getAuthorName(author_pcms_id));
        }
        return author;
    }
    private String getAuthorRightsFile(Row row) {
        return row.getCell(SpreadsheetReader.getColumnNameMap().get("Author Rights File")).getStringCellValue();
    }

}
