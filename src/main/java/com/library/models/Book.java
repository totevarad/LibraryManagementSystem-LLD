package com.library.models;

import com.library.patterns.observer.Observer;
import com.library.patterns.observer.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Book implements Subject {
    private static final Logger logger = LoggerFactory.getLogger(Book.class);
    
    private String title;
    private String author;
    private String isbn;
    private int publicationYear;
    private boolean isAvailable;
    private List<Observer> reservationQueue;

    public Book(String title, String author, String isbn, int publicationYear) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.isAvailable = true;
        this.reservationQueue = new ArrayList<>();
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }
    
    public boolean isAvailable() { return isAvailable; }
    
    public void setAvailable(boolean available) { 
        this.isAvailable = available; 
        if (this.isAvailable) {
            notifyObservers("Book '" + this.title + "' is now available for checkout.");
        }
    }

    @Override
    public void registerObserver(Observer observer) {
        if (!reservationQueue.contains(observer)) {
            reservationQueue.add(observer);
            logger.info("Observer added to reservation queue for book: {}", title);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        reservationQueue.remove(observer);
        logger.info("Observer removed from reservation queue for book: {}", title);
    }

    @Override
    public void notifyObservers(String message) {
        for (Observer observer : reservationQueue) {
            observer.update(message);
        }
        // Assuming after notifying, the first person might get it, but we'll notify all for simplicity
        // In a real system, you might notify the first in queue.
        reservationQueue.clear(); 
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", year=" + publicationYear +
                ", available=" + isAvailable +
                '}';
    }
}
