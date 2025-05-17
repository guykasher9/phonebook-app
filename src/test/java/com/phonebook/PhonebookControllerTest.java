package com.phonebook;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PhonebookController.class)
public class PhonebookControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ContactService contactService;

    @Test
    void testGetContacts() throws Exception {
        List<ContactDTO> contacts = List.of(new ContactDTO("John", "Doe", "1234567890", "123 Main St"));
        when(contactService.getContacts(0, 10)).thenReturn(contacts);
        mockMvc.perform(get("/api/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    void testSearchContactFound() throws Exception {
        ContactDTO contact = new ContactDTO("John", "Doe", "1234567890", "123 Main St");
        when(contactService.searchContactByName("John", "Doe")).thenReturn(Optional.of(contact));
        mockMvc.perform(get("/api/contacts/search?firstName=John&lastName=Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void testSearchContactNotFound() throws Exception {
        when(contactService.searchContactByName("John", "Doe")).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/contacts/search?firstName=John&lastName=Doe"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddContact() throws Exception {
        ContactDTO contact = new ContactDTO("John", "Doe", "1234567890", "123 Main St");
        when(contactService.addContact(any())).thenReturn(contact);
        String json = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"phoneNumber\":\"1234567890\",\"address\":\"123 Main St\"}";
        mockMvc.perform(post("/api/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void testEditContactFound() throws Exception {
        ContactDTO contact = new ContactDTO("John", "Doe", "1234567890", "123 Main St");
        when(contactService.editContact(eq("John_Doe"), any())).thenReturn(Optional.of(contact));
        String json = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"phoneNumber\":\"1234567890\",\"address\":\"123 Main St\"}";
        mockMvc.perform(put("/api/contacts/John_Doe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void testEditContactNotFound() throws Exception {
        when(contactService.editContact(eq("John_Doe"), any())).thenReturn(Optional.empty());
        String json = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"phoneNumber\":\"1234567890\",\"address\":\"123 Main St\"}";
        mockMvc.perform(put("/api/contacts/John_Doe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteContact() throws Exception {
        doNothing().when(contactService).deleteContact("John_Doe");
        mockMvc.perform(delete("/api/contacts/John_Doe"))
                .andExpect(status().isOk());
    }
} 