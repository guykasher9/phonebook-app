package com.phonebook;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ContactDTOTest {
    @Test
    void testConstructorAndGettersSetters() {
        ContactDTO contact = new ContactDTO("John", "Doe", "1234567890", "123 Main St");
        assertEquals("John", contact.getFirstName());
        assertEquals("Doe", contact.getLastName());
        assertEquals("1234567890", contact.getPhoneNumber());
        assertEquals("123 Main St", contact.getAddress());
        assertNotNull(contact.getId());
        assertFalse(contact.getId().isEmpty());

        contact.setFirstName("Jane");
        contact.setLastName("Smith");
        contact.setPhoneNumber("0987654321");
        contact.setAddress("456 Elm St");
        contact.setId("Jane_Smith");
        assertEquals("Jane", contact.getFirstName());
        assertEquals("Smith", contact.getLastName());
        assertEquals("0987654321", contact.getPhoneNumber());
        assertEquals("456 Elm St", contact.getAddress());
        assertEquals("Jane_Smith", contact.getId());
    }
} 