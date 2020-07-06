package com.upgrade.campsite.dto;

import com.upgrade.campsite.exceptions.CampsiteException;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class CancellationResponse {
    private CancellationRequest cancellationRequest;
    List<LocalDate> cancelledDates;
    String firstName;
    String lastName;
    String email;
    LocalDate checkInDate;
    LocalDate checkOutDate;
    private List<String> errors;
}
