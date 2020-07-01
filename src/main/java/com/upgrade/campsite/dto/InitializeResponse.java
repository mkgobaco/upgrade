package com.upgrade.campsite.dto;

import com.upgrade.campsite.exceptions.CampsiteException;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class InitializeResponse {
    private List<LocalDate> availableDates;
    private InitializeRequest initializeRequest;
    private List<String> errors;
}
