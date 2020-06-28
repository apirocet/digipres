package org.apirocet.digipres.mapper;

import org.apache.poi.ss.usermodel.Row;
import org.apirocet.digipres.SpreadsheetReader;
import org.apirocet.digipres.model.Author;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class AuthorMapper {

    private static final Logger LOGGER = getLogger(AuthorMapper.class);

    public Author mapRowToAuthor(Row row, int author_pcms_id) {
        Author author = new Author();

        author.setPcmsId(author_pcms_id);
        String rights_file = getAuthorRightsFile(row);
        author.setRightsFile(rights_file);

        return author;
    }
    private String getAuthorRightsFile(Row row) {
        return row.getCell(SpreadsheetReader.getColumnNameMap().get("Poet Rights File")).getStringCellValue();
    }

}
