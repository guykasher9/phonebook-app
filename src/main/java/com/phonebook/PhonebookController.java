package com.phonebook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Optional;
import java.util.Collections;
import java.util.HashMap;

@RestController
@RequestMapping("/api/contacts")
public class PhonebookController {
    private final ContactService contactService;

    @Autowired
    public PhonebookController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public ResponseEntity<List<ContactDTO>> getContacts(@RequestParam(defaultValue = "0") int page) {
        List<ContactDTO> contacts = contactService.getContacts(page, 10);
        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchContact(@RequestParam String firstName, @RequestParam String lastName, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        List<ContactDTO> results = contactService.searchContactByName(firstName, lastName, page, size);
        int total = contactService.countByFirstNameAndLastName(firstName, lastName);
        if (results.isEmpty()) {
            return ResponseEntity.status(404).body(results);
        }
        if (total > size) {
            return ResponseEntity.ok().body(new java.util.HashMap<>() {{
                put("message", "More than " + size + " contacts found. Showing first " + size + ".");
                put("contacts", results);
                put("total", total);
            }});
        }
        return ResponseEntity.ok(results);
    }

    @PostMapping
    public ResponseEntity<ContactDTO> addContact(@RequestBody ContactDTO contact) {
        ContactDTO saved = contactService.addContact(contact);
        return ResponseEntity.ok(saved);
    }

    private List<ContactDTO> resolveContacts(String id, String firstName, String lastName) {
        if (id != null) {
            return contactService.findContactById(id).map(List::of).orElse(Collections.emptyList());
        } else if (firstName != null && lastName != null) {
            return contactService.findContactsByName(firstName, lastName);
        } else {
            return Collections.emptyList();
        }
    }

    @PostMapping("/edit")
    public ResponseEntity<?> editContact(@RequestParam(required = false) String id,
                                         @RequestParam(required = false) String firstName,
                                         @RequestParam(required = false) String lastName,
                                         @RequestBody ContactDTO contact) {
        List<ContactDTO> matches = resolveContacts(id, firstName, lastName);
        if (matches.isEmpty()) {
            return ResponseEntity.status(404).body("Contact not found");
        } else if (matches.size() == 1) {
            Optional<ContactDTO> result = contactService.editContact(matches.get(0).getId(), contact);
            if (result.isPresent()) {
                return ResponseEntity.ok(result.get());
            } else {
                return ResponseEntity.status(404).body("Contact not found");
            }
        } else {
            return ResponseEntity.status(409).body(new HashMap<>() {{
                put("message", "Multiple contacts found. Please specify id.");
                put("contacts", matches);
            }});
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteContact(@RequestParam(required = false) String id,
                                           @RequestParam(required = false) String firstName,
                                           @RequestParam(required = false) String lastName) {
        List<ContactDTO> matches = resolveContacts(id, firstName, lastName);
        if (matches.isEmpty()) {
            return ResponseEntity.status(404).body("Contact not found");
        } else if (matches.size() == 1) {
            boolean deleted = contactService.deleteContactById(matches.get(0).getId());
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(404).body("Contact not found");
            }
        } else {
            return ResponseEntity.status(409).body(new HashMap<>() {{
                put("message", "Multiple contacts found. Please specify id.");
                put("contacts", matches);
            }});
        }
    }
} 