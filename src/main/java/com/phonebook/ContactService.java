package com.phonebook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;

@Service
public class ContactService {
    private final ContactRepository contactRepository;
    private final Counter addCounter, searchCounter, deleteCounter, editCounter, listCounter, failedCounter, emptyCounter;
    private final Timer searchTimer;

    @Autowired
    public ContactService(ContactRepository contactRepository, MeterRegistry registry) {
        this.contactRepository = contactRepository;
        this.addCounter = registry.counter("phonebook.requests", "type", "add");
        this.searchCounter = registry.counter("phonebook.requests", "type", "search");
        this.deleteCounter = registry.counter("phonebook.requests", "type", "delete");
        this.editCounter = registry.counter("phonebook.requests", "type", "edit");
        this.listCounter = registry.counter("phonebook.requests", "type", "list");
        this.failedCounter = registry.counter("phonebook.requests.failed");
        this.emptyCounter = registry.counter("phonebook.requests.empty");
        this.searchTimer = registry.timer("phonebook.search.timer");
    }

    public List<ContactDTO> getContacts(int page, int size) {
        listCounter.increment();
        return StreamSupport.stream(contactRepository.findAll().spliterator(), false)
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    public List<ContactDTO> searchContacts(String query) {
        return StreamSupport.stream(contactRepository.findAll().spliterator(), false)
                .filter(c -> c.getFirstName().contains(query) || c.getLastName().contains(query) || c.getPhoneNumber().contains(query) || c.getAddress().contains(query))
                .collect(Collectors.toList());
    }

    public ContactDTO addContact(ContactDTO contact) {
        addCounter.increment();
        contact.setId(contact.getFirstName() + "_" + contact.getLastName());
        return contactRepository.save(contact);
    }

    public Optional<ContactDTO> editContact(String id, ContactDTO contact) {
        editCounter.increment();
        try {
            if (contactRepository.existsById(id)) {
                contact.setId(id);
                return Optional.of(contactRepository.save(contact));
            }
            emptyCounter.increment();
            return Optional.empty();
        } catch (Exception e) {
            failedCounter.increment();
            throw e;
        }
    }

    public void deleteContact(String id) {
        deleteCounter.increment();
        try {
            contactRepository.deleteById(id);
        } catch (Exception e) {
            failedCounter.increment();
            throw e;
        }
    }

    public Optional<ContactDTO> getContactById(String id) {
        try {
            Optional<ContactDTO> result = contactRepository.findById(id);
            if (result.isEmpty()) emptyCounter.increment();
            return result;
        } catch (Exception e) {
            failedCounter.increment();
            throw e;
        }
    }

    public Optional<ContactDTO> searchContactByName(String firstName, String lastName) {
        searchCounter.increment();
        String id = firstName + "_" + lastName;
        return searchTimer.record(() -> {
            Optional<ContactDTO> result = getContactById(id);
            if (result.isEmpty()) emptyCounter.increment();
            return result;
        });
    }
} 