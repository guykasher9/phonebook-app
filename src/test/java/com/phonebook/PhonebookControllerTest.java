package com.phonebook;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class PhonebookControllerTest {
    @Mock
    private ContactService contactService;

    @InjectMocks
    private PhonebookController phonebookController;

    @SuppressWarnings("null")
    @Test
    void testGetContacts() {
        List<ContactDTO> contacts = List.of(new ContactDTO("John", "Doe", "1234567890", "123 Main St"));
        when(contactService.getContacts(0, 10)).thenReturn(contacts);
        ResponseEntity<List<ContactDTO>> response = phonebookController.getContacts(0);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().get(0).getFirstName());
    }

    @SuppressWarnings({"null", "unchecked"})
    @Test
    void testSearchContactFound() {
        ContactDTO contact = new ContactDTO("John", "Doe", "1234567890", "123 Main St");
        when(contactService.searchContactByName("John", "Doe", 0, 10)).thenReturn(List.of(contact));
        when(contactService.countByFirstNameAndLastName("John", "Doe")).thenReturn(1);
        ResponseEntity<?> response = phonebookController.searchContact("John", "Doe", 0, 10);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        List<ContactDTO> body = (List<ContactDTO>) response.getBody();
        assertEquals("John", body.get(0).getFirstName());
    }

    @Test
    void testSearchContactFoundWithPagination() {
        List<ContactDTO> contacts = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            contacts.add(new ContactDTO("John", "Doe", "12345" + i, "Address" + i));
        }
        when(contactService.searchContactByName("John", "Doe", 0, 10)).thenReturn(contacts.subList(0, 10));
        when(contactService.countByFirstNameAndLastName("John", "Doe")).thenReturn(15);
        ResponseEntity<?> response = phonebookController.searchContact("John", "Doe", 0, 10);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testSearchContactFoundLessThanPage() {
        List<ContactDTO> contacts = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            contacts.add(new ContactDTO("Jane", "Smith", "54321" + i, "Address" + i));
        }
        when(contactService.searchContactByName("Jane", "Smith", 0, 10)).thenReturn(contacts);
        when(contactService.countByFirstNameAndLastName("Jane", "Smith")).thenReturn(5);
        ResponseEntity<?> response = phonebookController.searchContact("Jane", "Smith", 0, 10);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testSearchContactNotFound() {
        when(contactService.searchContactByName("John", "Doe", 0, 10)).thenReturn(Collections.emptyList());
        when(contactService.countByFirstNameAndLastName("John", "Doe")).thenReturn(0);
        ResponseEntity<?> response = phonebookController.searchContact("John", "Doe", 0, 10);
        assertEquals(404, response.getStatusCode().value());
    }

    @SuppressWarnings("null")
    @Test
    void testAddContact() {
        ContactDTO contact = new ContactDTO("John", "Doe", "1234567890", "123 Main St");
        when(contactService.addContact(any())).thenReturn(contact);
        ResponseEntity<ContactDTO> response = phonebookController.addContact(contact);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getFirstName());
    }

    @SuppressWarnings("null")
    @Test
    void testEditContactFound() {
        ContactDTO contact = new ContactDTO("John", "Doe", "1234567890", "123 Main St");
        contact.setId("some-id");
        when(contactService.findContactById("some-id")).thenReturn(Optional.of(contact));
        when(contactService.editContact(eq("some-id"), any())).thenReturn(Optional.of(contact));
        ResponseEntity<?> response = phonebookController.editContact("some-id", null, null, contact);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("John", ((ContactDTO) response.getBody()).getFirstName());
    }

    @Test
    void testEditContactNotFound() {
        ContactDTO contact = new ContactDTO("John", "Doe", "1234567890", "123 Main St");
        ResponseEntity<?> response = phonebookController.editContact("John_Doe", null, null, contact);
        assertEquals(404, response.getStatusCode().value());
    }

    @SuppressWarnings("null")
    @Test
    void testEditContactByIdFound() {
        ContactDTO contact = new ContactDTO("John", "Doe", "1234567890", "123 Main St");
        contact.setId("some-id");
        when(contactService.findContactById("some-id")).thenReturn(Optional.of(contact));
        when(contactService.editContact(eq("some-id"), any())).thenReturn(Optional.of(contact));
        ResponseEntity<?> response = phonebookController.editContact("some-id", null, null, contact);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("John", ((ContactDTO) response.getBody()).getFirstName());
    }

    @Test
    void testEditContactByIdNotFound() {
        when(contactService.findContactById(anyString())).thenReturn(Optional.empty());
        ContactDTO contact = new ContactDTO("John", "Doe", "1234567890", "123 Main St");
        ResponseEntity<?> response = phonebookController.editContact("John_Doe", null, null, contact);
        assertEquals(404, response.getStatusCode().value());
    }

    @SuppressWarnings("null")
    @Test
    void testEditContactByNameOneMatch() {
        ContactDTO contact = new ContactDTO("John", "Doe", "1234567890", "123 Main St");
        when(contactService.findContactsByName("John", "Doe")).thenReturn(List.of(contact));
        when(contactService.editContact(eq(contact.getId()), any())).thenReturn(Optional.of(contact));
        ResponseEntity<?> response = phonebookController.editContact(null, "John", "Doe", contact);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("John", ((ContactDTO) response.getBody()).getFirstName());
    }

    @Test
    void testEditContactByNameMultipleMatches() {
        List<ContactDTO> contacts = Arrays.asList(
            new ContactDTO("John", "Doe", "1234567890", "123 Main St"),
            new ContactDTO("John", "Doe", "1234567891", "456 Elm St")
        );
        when(contactService.findContactsByName("John", "Doe")).thenReturn(contacts);
        ContactDTO contact = new ContactDTO("John", "Doe", "1234567890", "123 Main St");
        ResponseEntity<?> response = phonebookController.editContact(null, "John", "Doe", contact);
        assertEquals(409, response.getStatusCode().value());
    }

    @Test
    void testEditContactByNameNotFound() {
        when(contactService.findContactsByName("John", "Doe")).thenReturn(Collections.emptyList());
        ContactDTO contact = new ContactDTO("John", "Doe", "1234567890", "123 Main St");
        ResponseEntity<?> response = phonebookController.editContact(null, "John", "Doe", contact);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testDeleteContact() {
        ContactDTO contact = new ContactDTO("John", "Doe", "1234567890", "123 Main St");
        contact.setId("some-id");
        when(contactService.findContactById("some-id")).thenReturn(Optional.of(contact));
        when(contactService.deleteContactById("some-id")).thenReturn(true);
        ResponseEntity<?> response = phonebookController.deleteContact("some-id", null, null);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testDeleteContactByIdFound() {
        ContactDTO contact = new ContactDTO("John", "Doe", "1234567890", "123 Main St");
        contact.setId("some-id");
        when(contactService.findContactById("some-id")).thenReturn(Optional.of(contact));
        when(contactService.deleteContactById("some-id")).thenReturn(true);
        ResponseEntity<?> response = phonebookController.deleteContact("some-id", null, null);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testDeleteContactByNameOneMatch() {
        ContactDTO contact = new ContactDTO("John", "Doe", "1234567890", "123 Main St");
        when(contactService.findContactsByName("John", "Doe")).thenReturn(List.of(contact));
        when(contactService.deleteContactById(contact.getId())).thenReturn(true);
        ResponseEntity<?> response = phonebookController.deleteContact(null, "John", "Doe");
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testDeleteContactByNameMultipleMatches() {
        List<ContactDTO> contacts = Arrays.asList(
            new ContactDTO("John", "Doe", "1234567890", "123 Main St"),
            new ContactDTO("John", "Doe", "1234567891", "456 Elm St")
        );
        when(contactService.findContactsByName("John", "Doe")).thenReturn(contacts);
        ResponseEntity<?> response = phonebookController.deleteContact(null, "John", "Doe");
        assertEquals(409, response.getStatusCode().value());
    }

    @Test
    void testDeleteContactByNameNotFound() {
        when(contactService.findContactsByName("John", "Doe")).thenReturn(Collections.emptyList());
        ResponseEntity<?> response = phonebookController.deleteContact(null, "John", "Doe");
        assertEquals(404, response.getStatusCode().value());
    }
} 