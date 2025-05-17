package com.phonebook;

import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface ContactRepository extends CrudRepository<ContactDTO, String> {
    // Additional query methods can be defined here if needed
    List<ContactDTO> findByFirstNameAndLastName(String firstName, String lastName);
    List<ContactDTO> findByFirstName(String firstName);
    List<ContactDTO> findByLastName(String lastName);
} 