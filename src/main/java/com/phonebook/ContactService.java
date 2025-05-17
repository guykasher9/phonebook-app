package com.phonebook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ContactService {
    private static final Logger logger = LoggerFactory.getLogger(ContactService.class);
    private final ContactRepository contactRepository;
    private final MetricService metricService;

    @Autowired
    public ContactService(ContactRepository contactRepository, MetricService metricService) {
        this.contactRepository = contactRepository;
        this.metricService = metricService;
    }

    public List<ContactDTO> getContacts(int page, int size) {
        logger.info("Fetching contacts: page={}, size={}", page, size);
        metricService.incrementList();
        return StreamSupport.stream(contactRepository.findAll().spliterator(), false)
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    public List<ContactDTO> searchContacts(String query) {
        logger.info("Searching contacts with query: {}", query);
        return StreamSupport.stream(contactRepository.findAll().spliterator(), false)
                .filter(c -> c.getFirstName().contains(query) || c.getLastName().contains(query) || c.getPhoneNumber().contains(query) || c.getAddress().contains(query))
                .collect(Collectors.toList());
    }

    public ContactDTO addContact(ContactDTO contact) {
        logger.info("Adding contact: {} {}", contact.getFirstName(), contact.getLastName());
        metricService.incrementAdd();
        return contactRepository.save(contact);
    }

    public Optional<ContactDTO> editContact(String id, ContactDTO contact) {
        logger.info("Editing contact with id: {}", id);
        metricService.incrementEdit();
        try {
            if (contactRepository.existsById(id)) {
                contact.setId(id);
                return Optional.of(contactRepository.save(contact));
            }
            metricService.incrementEmpty();
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error editing contact with id: {}", id, e);
            metricService.incrementFailed();
            throw e;
        }
    }

    public boolean deleteContactById(String id) {
        logger.info("Deleting contact with id: {}", id);
        metricService.incrementDelete();
        try {
            if (contactRepository.existsById(id)) {
                contactRepository.deleteById(id);
                return true;
            } else {
                metricService.incrementEmpty();
                return false;
            }
        } catch (Exception e) {
            logger.error("Error deleting contact with id: {}", id, e);
            metricService.incrementFailed();
            throw e;
        }
    }

    public Optional<ContactDTO> getContactById(String id) {
        logger.info("Fetching contact by id: {}", id);
        try {
            Optional<ContactDTO> result = contactRepository.findById(id);
            if (result.isEmpty()) metricService.incrementEmpty();
            return result;
        } catch (Exception e) {
            logger.error("Error fetching contact by id: {}", id, e);
            metricService.incrementFailed();
            throw e;
        }
    }

    public List<ContactDTO> searchContactByName(String firstName, String lastName, int page, int size) {
        logger.info("Searching contacts by name: {} {}, page={}, size={}", firstName, lastName, page, size);
        metricService.incrementSearch();
        List<ContactDTO> results = contactRepository.findByFirstNameAndLastName(firstName, lastName);
        return results.stream()
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    public int countByFirstNameAndLastName(String firstName, String lastName) {
        logger.info("Counting contacts by name: {} {}", firstName, lastName);
        return contactRepository.findByFirstNameAndLastName(firstName, lastName).size();
    }

    public Optional<ContactDTO> findContactById(String id) {
        logger.info("Finding contact by id: {}", id);
        try {
            Optional<ContactDTO> result = contactRepository.findById(id);
            if (result.isEmpty()) metricService.incrementEmpty();
            return result;
        } catch (Exception e) {
            logger.error("Error finding contact by id: {}", id, e);
            metricService.incrementFailed();
            throw e;
        }
    }

    public List<ContactDTO> findContactsByName(String firstName, String lastName) {
        logger.info("Finding contacts by name: {} {}", firstName, lastName);
        metricService.incrementSearch();
        return contactRepository.findByFirstNameAndLastName(firstName, lastName);
    }
} 