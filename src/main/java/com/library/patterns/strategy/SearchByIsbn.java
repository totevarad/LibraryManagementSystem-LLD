package com.library.patterns.strategy;

import com.library.models.Book;
import java.util.List;
import java.util.stream.Collectors;

public class SearchByIsbn implements SearchStrategy {
    @Override
    public List<Book> search(List<Book> books, String query) {
        return books.stream()
                .filter(b -> b.getIsbn().equals(query))
                .collect(Collectors.toList());
    }
}
