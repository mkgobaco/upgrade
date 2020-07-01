package com.upgrade.campsite.services;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
public class IdServiceTest {

    @Autowired
    private IdService idService;

    @Test
    public void generateId() {
        String bookingId = idService.generateId(5);
        assertEquals(5, bookingId.length());
    }

}