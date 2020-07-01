package com.upgrade.campsite.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReservationResponse {
    private String bookingId;
    private ReservationRequest reservationRequest;
    private List<String> errors;
}
