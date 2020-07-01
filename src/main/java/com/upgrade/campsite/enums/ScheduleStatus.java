package com.upgrade.campsite.enums;

public enum ScheduleStatus {

    AVAILABLE("Available"),
    NOT_AVAILABLE("Not Available");

    ScheduleStatus(String message) {
        this.message = message;
    };

    String message;

}
