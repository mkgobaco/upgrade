package com.upgrade.campsite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
public class InitializeRequest {
    private LocalDate availableStartDate;
    private LocalDate availableEndDate;
}
