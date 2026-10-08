package com.library.services;

import com.library.models.Book;
import com.library.patterns.strategy.SearchStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class InventoryService {
    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);
    private List<Book> inventory;

    public InventoryService() {
        this.inventory = new ArrayList<>();
    }

    public void addBook(Book book) {
        inventory.add(book);
        logger.info("Book added to inventory: {}", book.getTitle());
    }

    public void removeBook(Book book) {
        if (inventory.remove(book)) {
            logger.info("Book removed from inventory: {}", book.getTitle());
        } else {
            logger.warn("Attempted to remove non-existent book: {}", book.getTitle());
        }
    }

    public void updateBook(String isbn, Book updatedBook) {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getIsbn().equals(isbn)) {
                inventory.set(i, updatedBook);
                logger.info("Book updated: {}", updatedBook.getTitle());
                return;
            }
        }
        logger.warn("Book with ISBN {} not found for update.", isbn);
    }

    public List<Book> searchBooks(SearchStrategy strategy, String query) {
        logger.debug("Searching books with query: {}", query);
        return strategy.search(inventory, query);
    }

    public List<Book> getInventory() {
        return inventory;
    }
}
