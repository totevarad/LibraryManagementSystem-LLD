package com.library.patterns.strategy;

import com.library.models.Book;
import java.util.List;

public interface SearchStrategy {
    List<Book> search(List<Book> books, String query);
}
