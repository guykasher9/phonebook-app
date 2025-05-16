package com.phonebook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class PhonebookController {
    private final ContactService contactService;

    @Autowired
    public PhonebookController(ContactService contactService) {
        this.contactService = contactService;
    }

    // Get contacts with pagination (max 10 per page)
    @GetMapping
    public ResponseEntity<List<ContactDTO>> getContacts(@RequestParam(defaultValue = "0") int page) {
        List<ContactDTO> contacts = contactService.getContacts(page, 10);
        return ResponseEntity.ok(contacts);
    }

    // Search contact by first and last name
    @GetMapping("/search")
    public ResponseEntity<?> searchContact(@RequestParam String firstName, @RequestParam String lastName) {
        return contactService.searchContactByName(firstName, lastName)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body("contact not found"));
    }

    // Add contact
    @PostMapping
    public ResponseEntity<ContactDTO> addContact(@RequestBody ContactDTO contact) {
        ContactDTO saved = contactService.addContact(contact);
        return ResponseEntity.ok(saved);
    }

    // Edit contact
    @PutMapping("/{id}")
    public ResponseEntity<ContactDTO> editContact(@PathVariable String id, @RequestBody ContactDTO contact) {
        return contactService.editContact(id, contact)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete contact
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable String id) {
        contactService.deleteContact(id);
        return ResponseEntity.ok().build();
    }
} 