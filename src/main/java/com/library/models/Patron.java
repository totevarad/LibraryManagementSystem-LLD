package com.library.models;

import com.library.patterns.observer.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Patron implements Observer {
    private static final Logger logger = LoggerFactory.getLogger(Patron.class);

    private String id;
    private String name;
    private String email;
    private List<Book> borrowingHistory;

    public Patron(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.borrowingHistory = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Book> getBorrowingHistory() { return borrowingHistory; }

    public void addToHistory(Book book) {
        borrowingHistory.add(book);
        logger.debug("Book '{}' added to {}'s borrowing history.", book.getTitle(), name);
    }

    @Override
    public void update(String message) {
        logger.info("Notification for Patron {} ({}): {}", name, email, message);
        System.out.println("Notification for " + name + ": " + message);
    }

    @Override
    public String toString() {
        return "Patron{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
