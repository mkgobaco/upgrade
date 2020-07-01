package com.upgrade.campsite.exceptions;

import com.upgrade.campsite.dto.ModificationRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ModificationException extends RuntimeException{
    private ModificationRequest modificationRequest;
    private List<String> errors;
}
