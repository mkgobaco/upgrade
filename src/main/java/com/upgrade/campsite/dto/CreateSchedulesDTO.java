package com.upgrade.campsite.dto;

import com.upgrade.campsite.entities.Schedule;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CreateSchedulesDTO {
    List<Schedule> schedules;
    List<String> errors;
}
