package com.phonebook;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RedisConfigTest {
    @Autowired
    private RedisTemplate<String, ContactDTO> redisTemplate;

    @Test
    void testRedisTemplateBeanExists() {
        assertThat(redisTemplate).isNotNull();
    }
} 