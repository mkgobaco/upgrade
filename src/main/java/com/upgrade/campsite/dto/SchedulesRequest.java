package com.upgrade.campsite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchedulesRequest {
    private LocalDate startDate;
    private LocalDate endDate;
}
