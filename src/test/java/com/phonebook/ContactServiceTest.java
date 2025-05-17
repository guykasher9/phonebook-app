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
        assertEquals("John_Doe", saved.getId());
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
    void testDeleteContact() {
        doNothing().when(contactRepository).deleteById(anyString());
        contactService.deleteContact("John_Doe");
        verify(metricService).incrementDelete();
    }

    @Test
    void testDeleteContactException() {
        doThrow(new RuntimeException()).when(contactRepository).deleteById(anyString());
        assertThrows(RuntimeException.class, () -> contactService.deleteContact("John_Doe"));
        verify(metricService).incrementDelete();
        verify(metricService).incrementFailed();
    }

    @Test
    void testGetContactByIdFound() {
        when(contactRepository.findById(anyString())).thenReturn(Optional.of(contact));
        Optional<ContactDTO> result = contactService.getContactById("John_Doe");
        assertTrue(result.isPresent());
    }

    @Test
    void testGetContactByIdNotFound() {
        when(contactRepository.findById(anyString())).thenReturn(Optional.empty());
        Optional<ContactDTO> result = contactService.getContactById("John_Doe");
        assertTrue(result.isEmpty());
        verify(metricService).incrementEmpty();
    }

    @Test
    void testGetContactByIdException() {
        when(contactRepository.findById(anyString())).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> contactService.getContactById("John_Doe"));
        verify(metricService).incrementFailed();
    }

    @Test
    void testSearchContactByName() {
        when(metricService.timeSearch(any())).then(invocation -> {
            java.util.function.Supplier<?> supplier = invocation.getArgument(0);
            return supplier.get();
        });
        when(contactRepository.findById(anyString())).thenReturn(Optional.of(contact));
        Optional<ContactDTO> result = contactService.searchContactByName("John", "Doe");
        assertTrue(result.isPresent());
        verify(metricService).incrementSearch();
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
} 