package com.upgrade.campsite.exceptions;

import com.upgrade.campsite.dto.CancellationResponse;
import com.upgrade.campsite.dto.ModificationResponse;
import com.upgrade.campsite.dto.ReservationResponse;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ControllerAdvice
public class CampsiteExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public final ResponseEntity<Object> handleAllExceptions(Throwable ex, WebRequest request) {
        return ResponseEntity.of(Optional.of(ex.getLocalizedMessage()));
    }

    @ExceptionHandler(ReservationException.class)
    public final ResponseEntity<Object> reservationException(ReservationException ex, WebRequest request) {
        ReservationResponse reservationResponse = ReservationResponse.builder()
                .reservationRequest(ex.getReservationRequest())
                .errors(ex.getErrors())
                .build();
        return ResponseEntity.of(Optional.of(reservationResponse));
    }

    @ExceptionHandler(ModificationException.class)
    public final ResponseEntity<Object> modificationException(ModificationException ex, WebRequest request) {
        ModificationResponse reservationResponse = ModificationResponse.builder()
                .modificationRequest(ex.getModificationRequest())
                .errors(ex.getErrors())
                .build();
        return ResponseEntity.of(Optional.of(reservationResponse));
    }

    @ExceptionHandler(CancellationException.class)
    public final ResponseEntity<Object> cancellationException(CancellationException ex, WebRequest request) {
        CancellationResponse reservationResponse = CancellationResponse.builder()
                .cancellationRequest(ex.getCancellationRequest())
                .errors(ex.getErrors())
                .build();
        return ResponseEntity.of(Optional.of(reservationResponse));
    }
}
