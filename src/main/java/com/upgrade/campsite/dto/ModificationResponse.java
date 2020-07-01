package com.upgrade.campsite.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ModificationResponse {
    private ModificationRequest modificationRequest;
    private CancellationResponse cancellationResponse;
    private ReservationResponse reservationResponse;
    private List<String> errors;
}
