package com.library;

import com.library.models.Book;
import com.library.models.Patron;
import com.library.patterns.factory.BookFactory;
import com.library.patterns.strategy.SearchByAuthor;
import com.library.patterns.strategy.SearchByIsbn;
import com.library.patterns.strategy.SearchByTitle;
import com.library.services.InventoryService;
import com.library.services.LendingService;
import com.library.services.PatronService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final Scanner scanner = new Scanner(System.in);
    
    private static final InventoryService inventoryService = new InventoryService();
    private static final PatronService patronService = new PatronService();
    private static final LendingService lendingService = new LendingService();

    // Regex constants
    private static final String ISBN_REGEX = "^[A-Za-z]{5}-\\d{4}-\\d{7}$";
    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final String PATRON_ID_REGEX = "^P\\d{3}$";

    public static void main(String[] args) {
        logger.info("Initializing Library Management System - Menu Driven");
        initializeData();

        boolean running = true;
        while (running) {
            System.out.println("\n=== Library Management System ===");
            System.out.println("1. Book Management (Add, Remove, Update, Search)");
            System.out.println("2. Patron Management (Add, Update, View History)");
            System.out.println("3. Lending Process (Checkout, Return)");
            System.out.println("4. View Inventory");
            System.out.println("0. Exit");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    bookManagementMenu();
                    break;
                case "2":
                    patronManagementMenu();
                    break;
                case "3":
                    lendingMenu();
                    break;
                case "4":
                    viewInventory();
                    break;
                case "0":
                    running = false;
                    System.out.println("Exiting the system. Goodbye!");
                    logger.info("System shutting down.");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void initializeData() {
        System.out.println("Pre-populating data...");
        // Updated initial data to match new ISBN format requirements
        Book book1 = BookFactory.createBook("1984", "George Orwell", "ORWEL-1949-1234567", 1949);
        Book book2 = BookFactory.createBook("To Kill a Mockingbird", "Harper Lee", "HARPE-1960-7654321", 1960);
        Book book3 = BookFactory.createBook("The Great Gatsby", "F. Scott Fitzgerald", "FITSZ-1925-1122334", 1925);
        inventoryService.addBook(book1);
        inventoryService.addBook(book2);
        inventoryService.addBook(book3);

        Patron patron1 = new Patron("P001", "Alice Smith", "alice@example.com");
        patronService.addPatron(patron1);
        System.out.println("Data initialized with 3 books and 1 patron (P001 - Alice Smith).\n");
    }

    private static String readString(String prompt, String regex, String errorMessage, String formatHint) {
        while (true) {
            System.out.print(prompt + (formatHint != null ? " [" + formatHint + "]: " : ": "));
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Error: Input cannot be empty.");
            } else if (regex != null && !input.matches(regex)) {
                System.out.println("Error: " + errorMessage);
            } else {
                return input;
            }
        }
    }

    private static int readInt(String prompt, int min, int max, String formatHint) {
        while (true) {
            System.out.print(prompt + (formatHint != null ? " [" + formatHint + "]: " : ": "));
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.println("Error: Please enter a number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid number format. Please enter a valid year.");
            }
        }
    }

    private static void bookManagementMenu() {
        System.out.println("\n--- Book Management ---");
        System.out.println("1. Add Book");
        System.out.println("2. Remove Book");
        System.out.println("3. Update Book");
        System.out.println("4. Search Book");
        System.out.println("5. View Books (Last 10)");
        System.out.print("Select an option: ");
        
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                String title = readString("Enter Title", null, null, null);
                String author = readString("Enter Author", null, null, null);
                String isbn = readString("Enter ISBN", ISBN_REGEX, "Format must be 5Letters-4Digits-7Digits", "e.g., PCALM-2026-1234567");
                int year = readInt("Enter Publication Year", 1000, 2100, "e.g., 2023");
                inventoryService.addBook(BookFactory.createBook(title, author, isbn, year));
                System.out.println("Book added successfully.");
                break;
            case "2":
                String removeIsbn = readString("Enter ISBN of the book to remove", ISBN_REGEX, "Format must be 5Letters-4Digits-7Digits", "e.g., PCALM-2026-1234567");
                List<Book> booksToRemove = inventoryService.searchBooks(new SearchByIsbn(), removeIsbn);
                if (!booksToRemove.isEmpty()) {
                    inventoryService.removeBook(booksToRemove.get(0));
                    System.out.println("Book removed successfully.");
                } else {
                    System.out.println("Book not found.");
                }
                break;
            case "3":
                String updateIsbn = readString("Enter ISBN of the book to update", ISBN_REGEX, "Format must be 5Letters-4Digits-7Digits", "e.g., PCALM-2026-1234567");
                if (inventoryService.searchBooks(new SearchByIsbn(), updateIsbn).isEmpty()) {
                    System.out.println("Book with this ISBN not found in inventory.");
                    break;
                }
                String newTitle = readString("Enter new Title", null, null, null);
                String newAuthor = readString("Enter new Author", null, null, null);
                int newYear = readInt("Enter new Publication Year", 1000, 2100, "e.g., 2023");
                inventoryService.updateBook(updateIsbn, BookFactory.createBook(newTitle, newAuthor, updateIsbn, newYear));
                break;
            case "4":
                System.out.println("Search by: 1. Title  2. Author  3. ISBN");
                String searchChoice = scanner.nextLine().trim();
                List<Book> results;
                if ("1".equals(searchChoice)) {
                    String query = readString("Enter title search query", null, null, null);
                    results = inventoryService.searchBooks(new SearchByTitle(), query);
                } else if ("2".equals(searchChoice)) {
                    String query = readString("Enter author search query", null, null, null);
                    results = inventoryService.searchBooks(new SearchByAuthor(), query);
                } else if ("3".equals(searchChoice)) {
                    String query = readString("Enter ISBN", ISBN_REGEX, "Format must be 5Letters-4Digits-7Digits", "e.g., PCALM-2026-1234567");
                    results = inventoryService.searchBooks(new SearchByIsbn(), query);
                } else {
                    System.out.println("Invalid search option.");
                    break;
                }
                System.out.println("Search Results:");
                results.forEach(System.out::println);
                break;
            case "5":
                List<Book> books = inventoryService.getInventory();
                if (books.isEmpty()) {
                    System.out.println("Inventory is empty.");
                } else {
                    int startIndex = Math.max(0, books.size() - 10);
                    if (startIndex > 0) {
                        System.out.println("Viewing last 10 books out of " + books.size() + " total books:");
                    } else {
                        System.out.println("Viewing books:");
                    }
                    for (int i = startIndex; i < books.size(); i++) {
                        System.out.println(books.get(i));
                    }
                }
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    private static void patronManagementMenu() {
        System.out.println("\n--- Patron Management ---");
        System.out.println("1. Add Patron");
        System.out.println("2. Update Patron");
        System.out.println("3. View Borrowing History");
        System.out.print("Select an option: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                String id = readString("Enter Patron ID", PATRON_ID_REGEX, "ID must start with P followed by 3 digits", "e.g., P001");
                String name = readString("Enter Name", null, null, null);
                String email = readString("Enter Email", EMAIL_REGEX, "Must be a valid email", "e.g., user@example.com");
                patronService.addPatron(new Patron(id, name, email));
                System.out.println("Patron added successfully.");
                break;
            case "2":
                String updateId = readString("Enter Patron ID to update", PATRON_ID_REGEX, "ID must start with P followed by 3 digits", "e.g., P001");
                Patron existingPatron = patronService.getPatron(updateId);
                if (existingPatron != null) {
                    String newName = readString("Enter new Name", null, null, null);
                    String newEmail = readString("Enter new Email", EMAIL_REGEX, "Must be a valid email", "e.g., user@example.com");
                    patronService.updatePatron(new Patron(updateId, newName, newEmail));
                    System.out.println("Patron updated successfully.");
                } else {
                    System.out.println("Patron not found.");
                }
                break;
            case "3":
                String historyId = readString("Enter Patron ID", PATRON_ID_REGEX, "ID must start with P followed by 3 digits", "e.g., P001");
                Patron p = patronService.getPatron(historyId);
                if (p != null) {
                    System.out.println("Borrowing History for " + p.getName() + ":");
                    p.getBorrowingHistory().forEach(System.out::println);
                } else {
                    System.out.println("Patron not found.");
                }
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    private static void lendingMenu() {
        System.out.println("\n--- Lending Process ---");
        System.out.println("1. Checkout Book");
        System.out.println("2. Return Book");
        System.out.print("Select an option: ");
        
        String choice = scanner.nextLine().trim();
        if ("1".equals(choice) || "2".equals(choice)) {
            String isbn = readString("Enter Book ISBN", ISBN_REGEX, "Format must be 5Letters-4Digits-7Digits", "e.g., PCALM-2026-1234567");
            List<Book> books = inventoryService.searchBooks(new SearchByIsbn(), isbn);
            if (books.isEmpty()) {
                System.out.println("Book not found in inventory.");
                return;
            }
            Book book = books.get(0);

            if ("1".equals(choice)) {
                String pid = readString("Enter Patron ID", PATRON_ID_REGEX, "ID must start with P followed by 3 digits", "e.g., P001");
                Patron patron = patronService.getPatron(pid);
                if (patron == null) {
                    System.out.println("Patron not found.");
                    return;
                }
                lendingService.checkoutBook(book, patron);
            } else {
                lendingService.returnBook(book);
            }
        } else {
            System.out.println("Invalid option.");
        }
    }

    private static void viewInventory() {
        System.out.println("\n--- Current Inventory ---");
        List<Book> books = inventoryService.getInventory();
        if (books.isEmpty()) {
            System.out.println("Inventory is empty.");
        } else {
            for (Book book : books) {
                System.out.println(book);
            }
        }
    }
}
