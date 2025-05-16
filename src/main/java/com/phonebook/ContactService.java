package com.phonebook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ContactService {
    private final ContactRepository contactRepository;
    private final MetricService metricService;

    @Autowired
    public ContactService(ContactRepository contactRepository, MetricService metricService) {
        this.contactRepository = contactRepository;
        this.metricService = metricService;
    }

    public List<ContactDTO> getContacts(int page, int size) {
        metricService.incrementList();
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
        metricService.incrementAdd();
        contact.setId(contact.getFirstName() + "_" + contact.getLastName());
        return contactRepository.save(contact);
    }

    public Optional<ContactDTO> editContact(String id, ContactDTO contact) {
        metricService.incrementEdit();
        try {
            if (contactRepository.existsById(id)) {
                contact.setId(id);
                return Optional.of(contactRepository.save(contact));
            }
            metricService.incrementEmpty();
            return Optional.empty();
        } catch (Exception e) {
            metricService.incrementFailed();
            throw e;
        }
    }

    public void deleteContact(String id) {
        metricService.incrementDelete();
        try {
            contactRepository.deleteById(id);
        } catch (Exception e) {
            metricService.incrementFailed();
            throw e;
        }
    }

    public Optional<ContactDTO> getContactById(String id) {
        try {
            Optional<ContactDTO> result = contactRepository.findById(id);
            if (result.isEmpty()) metricService.incrementEmpty();
            return result;
        } catch (Exception e) {
            metricService.incrementFailed();
            throw e;
        }
    }

    public Optional<ContactDTO> searchContactByName(String firstName, String lastName) {
        metricService.incrementSearch();
        String id = firstName + "_" + lastName;
        return metricService.timeSearch(() -> {
            Optional<ContactDTO> result = getContactById(id);
            if (result.isEmpty()) metricService.incrementEmpty();
            return result;
        });
    }
} 