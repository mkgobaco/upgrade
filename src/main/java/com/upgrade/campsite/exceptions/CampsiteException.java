package com.upgrade.campsite.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CampsiteException extends RuntimeException {
    private List<String> errors;
}
