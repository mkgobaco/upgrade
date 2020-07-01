package com.upgrade.campsite.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CancellationRequestTest {

    @Test
    void builder() {
        CancellationRequest cancellationRequest = CancellationRequest.builder().bookingId("123").build();
        assertNotNull(cancellationRequest);
    }
}