package com.upgrade.campsite.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class SchedulesResponse {
    private List<LocalDate> availableDates;
    private SchedulesRequest schedulesRequest;
    private List<String> errors;
}
