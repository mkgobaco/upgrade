package com.upgrade.campsite.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ReservationRequest {
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
