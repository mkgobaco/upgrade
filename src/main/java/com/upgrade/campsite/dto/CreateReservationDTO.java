package com.upgrade.campsite.dto;

import com.upgrade.campsite.entities.Reservation;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CreateReservationDTO {
    Reservation reservation;
    List<String> errors;
}
