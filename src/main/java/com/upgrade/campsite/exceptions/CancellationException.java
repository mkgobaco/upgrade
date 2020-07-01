package com.upgrade.campsite.exceptions;

import com.upgrade.campsite.dto.CancellationRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CancellationException extends RuntimeException{
    private CancellationRequest cancellationRequest;
    private List<String> errors;
}
