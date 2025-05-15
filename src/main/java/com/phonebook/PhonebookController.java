package com.phonebook;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class PhonebookController {

    // Get contacts with pagination (max 10 per page)
    @GetMapping
    public ResponseEntity<List<Object>> getContacts(@RequestParam(defaultValue = "0") int page) {
        // Logic to be implemented
        return ResponseEntity.ok().build();
    }

    // Search contact
    @GetMapping("/search")
    public ResponseEntity<List<Object>> searchContacts(@RequestParam String query) {
        // Logic to be implemented
        return ResponseEntity.ok().build();
    }

    // Add contact
    @PostMapping
    public ResponseEntity<Object> addContact(@RequestBody Object contact) {
        // Logic to be implemented
        return ResponseEntity.ok().build();
    }

    // Edit contact
    @PutMapping("/{id}")
    public ResponseEntity<Object> editContact(@PathVariable String id, @RequestBody Object contact) {
        // Logic to be implemented
        return ResponseEntity.ok().build();
    }

    // Delete contact
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable String id) {
        // Logic to be implemented
        return ResponseEntity.ok().build();
    }
} 