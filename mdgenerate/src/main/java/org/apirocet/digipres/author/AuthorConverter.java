package org.apirocet.digipres.author;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.util.List;
import java.util.stream.Collectors;

public class AuthorConverter extends StdConverter<List<AuthorModel>, List<String>> {
    @Override
    public List<String> convert(List<AuthorModel> authors) {
        return authors.stream().map(AuthorModel::getName).collect(Collectors.toList());
    }
}
