package com.upgrade.campsite.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InitializeRequestTest {

    @Test
    void builder() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(1);
        InitializeRequest initializeRequest = InitializeRequest.builder()
                .availableStartDate(startDate)
                .availableEndDate(endDate)
                .build();
    }
}