package org.apirocet.digipres.author;

import org.apache.poi.ss.usermodel.Row;
import org.apirocet.digipres.SpreadsheetReader;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class AuthorMapper {

    private static final Logger LOGGER = getLogger(AuthorMapper.class);

    public AuthorModel mapRowToAuthor(Row row, int author_pcms_id) {
        AuthorModel author = new AuthorModel();

        author.setPcmsId(author_pcms_id);
        String rights_file = getAuthorRightsFile(row);
        author.setRightsFile(rights_file);

        return author;
    }
    private String getAuthorRightsFile(Row row) {
        return row.getCell(SpreadsheetReader.getColumnNameMap().get("Poet Rights File")).getStringCellValue();
    }

}
