package com.library.services;

import com.library.models.Book;
import com.library.models.Patron;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LendingService {
    private static final Logger logger = LoggerFactory.getLogger(LendingService.class);

    public void checkoutBook(Book book, Patron patron) {
        if (book.isAvailable()) {
            book.setAvailable(false);
            patron.addToHistory(book);
            logger.info("Book '{}' checked out by Patron '{}'", book.getTitle(), patron.getName());
        } else {
            logger.warn("Book '{}' is not available for checkout.", book.getTitle());
            System.out.println("Book is currently unavailable. Would you like to reserve it?");
            // We can register patron as observer directly
            reserveBook(book, patron);
        }
    }

    public void returnBook(Book book) {
        if (!book.isAvailable()) {
            book.setAvailable(true); // This will trigger notifyObservers() inside Book
            logger.info("Book '{}' has been returned.", book.getTitle());
        } else {
            logger.warn("Attempted to return a book that is already available: {}", book.getTitle());
        }
    }

    public void reserveBook(Book book, Patron patron) {
        if (!book.isAvailable()) {
            book.registerObserver(patron);
            logger.info("Patron '{}' reserved book '{}'", patron.getName(), book.getTitle());
            System.out.println(patron.getName() + " has successfully reserved " + book.getTitle());
        } else {
            logger.info("Book '{}' is available. No need to reserve.", book.getTitle());
        }
    }
}
