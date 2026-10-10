package com.library.services;

import com.library.models.Patron;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class PatronService {
    private static final Logger logger = LoggerFactory.getLogger(PatronService.class);
    private Map<String, Patron> patrons;

    public PatronService() {
        this.patrons = new HashMap<>();
    }

    public void addPatron(Patron patron) {
        if (!patrons.containsKey(patron.getId())) {
            patrons.put(patron.getId(), patron);
            logger.info("Patron added: {}", patron.getName());
        } else {
            logger.warn("Patron with ID {} already exists.", patron.getId());
        }
    }

    public void updatePatron(Patron patron) {
        if (patrons.containsKey(patron.getId())) {
            patrons.put(patron.getId(), patron);
            logger.info("Patron updated: {}", patron.getName());
        } else {
            logger.warn("Patron with ID {} not found for update.", patron.getId());
        }
    }

    public Patron getPatron(String id) {
        return patrons.get(id);
    }
}
