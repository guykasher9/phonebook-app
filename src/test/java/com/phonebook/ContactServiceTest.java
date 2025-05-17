package com.phonebook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ContactServiceTest {
    @Mock
    private ContactRepository contactRepository;
    @Mock
    private MetricService metricService;
    @InjectMocks
    private ContactService contactService;

    private ContactDTO contact;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        contact = new ContactDTO("John", "Doe", "1234567890", "123 Main St");
    }

    @Test
    void testGetContacts() {
        List<ContactDTO> contacts = Arrays.asList(contact);
        when(contactRepository.findAll()).thenReturn(contacts);
        List<ContactDTO> result = contactService.getContacts(0, 10);
        assertEquals(1, result.size());
        verify(metricService).incrementList();
    }

    @Test
    void testSearchContacts() {
        List<ContactDTO> contacts = Arrays.asList(contact);
        when(contactRepository.findAll()).thenReturn(contacts);
        List<ContactDTO> result = contactService.searchContacts("John");
        assertEquals(1, result.size());
    }

    @Test
    void testAddContact() {
        when(contactRepository.save(any(ContactDTO.class))).thenReturn(contact);
        ContactDTO saved = contactService.addContact(contact);
        assertNotNull(saved.getId());
        assertFalse(saved.getId().isEmpty());
        verify(metricService).incrementAdd();
    }

    @Test
    void testEditContactExists() {
        when(contactRepository.existsById(anyString())).thenReturn(true);
        when(contactRepository.save(any(ContactDTO.class))).thenReturn(contact);
        Optional<ContactDTO> result = contactService.editContact("John_Doe", contact);
        assertTrue(result.isPresent());
        verify(metricService).incrementEdit();
    }

    @Test
    void testEditContactNotExists() {
        when(contactRepository.existsById(anyString())).thenReturn(false);
        Optional<ContactDTO> result = contactService.editContact("John_Doe", contact);
        assertTrue(result.isEmpty());
        verify(metricService).incrementEdit();
        verify(metricService).incrementEmpty();
    }

    @Test
    void testEditContactException() {
        when(contactRepository.existsById(anyString())).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> contactService.editContact("John_Doe", contact));
        verify(metricService).incrementEdit();
        verify(metricService).incrementFailed();
    }

    @Test
    void testGetContactsPagination() {
        List<ContactDTO> contacts = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            contacts.add(new ContactDTO("Name" + i, "Surname" + i, "12345" + i, "Address" + i));
        }
        when(contactRepository.findAll()).thenReturn(contacts);

        // Page 0, size 10: should return contacts 0-9
        List<ContactDTO> page0 = contactService.getContacts(0, 10);
        assertEquals(10, page0.size());
        assertEquals("Name0", page0.get(0).getFirstName());
        assertEquals("Name9", page0.get(9).getFirstName());

        // Page 1, size 10: should return contacts 10-19
        List<ContactDTO> page1 = contactService.getContacts(1, 10);
        assertEquals(10, page1.size());
        assertEquals("Name10", page1.get(0).getFirstName());
        assertEquals("Name19", page1.get(9).getFirstName());

        // Page 2, size 10: should return contacts 20-24 (5 contacts)
        List<ContactDTO> page2 = contactService.getContacts(2, 10);
        assertEquals(5, page2.size());
        assertEquals("Name20", page2.get(0).getFirstName());
        assertEquals("Name24", page2.get(4).getFirstName());
    }

    @Test
    void testSearchContactByNamePagination() {
        List<ContactDTO> contacts = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            contacts.add(new ContactDTO("John", "Doe", "12345" + i, "Address" + i));
        }
        when(contactRepository.findByFirstNameAndLastName("John", "Doe")).thenReturn(contacts);

        // Page 0, size 10
        List<ContactDTO> page0 = contactService.searchContactByName("John", "Doe", 0, 10);
        assertEquals(10, page0.size());
        assertEquals("123450", page0.get(0).getPhoneNumber());
        assertEquals("123459", page0.get(9).getPhoneNumber());

        // Page 1, size 10
        List<ContactDTO> page1 = contactService.searchContactByName("John", "Doe", 1, 10);
        assertEquals(5, page1.size());
        assertEquals("1234510", page1.get(0).getPhoneNumber());
        assertEquals("1234514", page1.get(4).getPhoneNumber());
    }

    @Test
    void testFindContactByIdFound() {
        when(contactRepository.findById("some-id")).thenReturn(Optional.of(contact));
        Optional<ContactDTO> result = contactService.findContactById("some-id");
        assertTrue(result.isPresent());
        assertEquals(contact, result.get());
    }

    @Test
    void testFindContactByIdNotFound() {
        when(contactRepository.findById("some-id")).thenReturn(Optional.empty());
        Optional<ContactDTO> result = contactService.findContactById("some-id");
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindContactsByName() {
        List<ContactDTO> contacts = Arrays.asList(
            new ContactDTO("John", "Doe", "1234567890", "123 Main St"),
            new ContactDTO("John", "Doe", "1234567891", "456 Elm St")
        );
        when(contactRepository.findByFirstNameAndLastName("John", "Doe")).thenReturn(contacts);
        List<ContactDTO> result = contactService.findContactsByName("John", "Doe");
        assertEquals(2, result.size());
    }

    @Test
    void testDeleteContactByIdFound() {
        when(contactRepository.existsById("some-id")).thenReturn(true);
        doNothing().when(contactRepository).deleteById("some-id");
        boolean deleted = contactService.deleteContactById("some-id");
        assertTrue(deleted);
    }

    @Test
    void testDeleteContactByIdNotFound() {
        when(contactRepository.existsById("some-id")).thenReturn(false);
        boolean deleted = contactService.deleteContactById("some-id");
        assertFalse(deleted);
    }

    @Test
    void testDeleteContactByIdException() {
        when(contactRepository.existsById("some-id")).thenReturn(true);
        doThrow(new RuntimeException()).when(contactRepository).deleteById("some-id");
        assertThrows(RuntimeException.class, () -> contactService.deleteContactById("some-id"));
    }
} 