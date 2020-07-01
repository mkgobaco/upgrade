package com.upgrade.campsite.enums;

public enum ReservationStatus {

    RESERVED("Reserved"),
    CANCELED("Cancelled"),
    MODIFIED("Modified");

    ReservationStatus(String message) {
        this.message = message;
    };

    String message;

}
