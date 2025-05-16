package com.phonebook;

import org.springframework.data.repository.CrudRepository;

public interface ContactRepository extends CrudRepository<ContactDTO, String> {
    // Additional query methods can be defined here if needed
} 