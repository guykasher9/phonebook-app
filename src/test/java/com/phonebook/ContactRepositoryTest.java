package com.phonebook;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ContactRepositoryTest {
    @Autowired
    private ContactRepository contactRepository;

    @Test
    void testRepositoryIsNotNull() {
        assertThat(contactRepository).isNotNull();
    }
} 