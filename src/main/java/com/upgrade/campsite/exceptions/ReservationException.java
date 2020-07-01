package com.upgrade.campsite.exceptions;

import com.upgrade.campsite.dto.ReservationRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ReservationException extends RuntimeException {
    private ReservationRequest reservationRequest;
    private List<String> errors;
}
