package com.upgrade.campsite.restservices;

import com.upgrade.campsite.dto.*;
import com.upgrade.campsite.entities.Reservation;
import com.upgrade.campsite.entities.Schedule;
import com.upgrade.campsite.exceptions.CampsiteException;
import com.upgrade.campsite.services.CampsiteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@Api(description = "Endpoints for Creating, Modifying and Cancelling Campsite Reservations",
        tags = {"campsite"})
public class CampsiteController {

    private CampsiteService campsiteService;

    @Autowired
    public CampsiteController(CampsiteService campsiteService) {
        this.campsiteService = campsiteService;
    }

    @ApiOperation(value = "Find Reservation by BookingId", notes = "Get Reservation record by BookingId", tags = { "reservation" })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response=Reservation.class )  })
    @GetMapping("/reservation")
    public Reservation reservation(@RequestParam ("bookingId") String bookingId) {

        return campsiteService.reservation(bookingId).get();
    }

    @ApiOperation(value = "Get All Reservations", notes = "Get All Reservations", tags = { "reservations" })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request Sent", response=List.class )  })
    @GetMapping("/reservations")
    public List<Reservation> reservations() {

        return campsiteService.reservations();
    }

    @ApiOperation(value = "Get All Schedules", notes = "Get All Schedules", tags = { "schedules" })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request Sent", response=List.class )  })
    @GetMapping("/schedules")
    public List<Schedule> schedules() {

        return campsiteService.schedules();
    }


    @ApiOperation(value = "Get Available Schedules", notes = "Get Available Schedules", tags = { "schedules" })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request Sent", response=SchedulesResponse.class )  })
    @PostMapping("/available")
    public SchedulesResponse available(@RequestBody SchedulesRequest schedulesRequest) {

        return campsiteService.available(schedulesRequest);

    }

    @ApiOperation(value = "Create Reservation", notes = "Create Reservation", tags = { "reserve" })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request Sent", response=ReservationResponse.class )  })
    @PostMapping("/reserve")
    public ReservationResponse reserve(@RequestBody ReservationRequest request) {

            return campsiteService.reserve(request);
    }

    @ApiOperation(value = "Modify Reservation", notes = "Modify Reservation", tags = { "modify" })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request Sent", response=ModificationResponse.class )  })
    @PostMapping("/modify")
    public ModificationResponse modify(@RequestBody ModificationRequest request) {

        ModificationResponse response = campsiteService.modify(request);

        return response;
    }

    @ApiOperation(value = "Cancel Reservation", notes = "Cancel Reservation", tags = { "cancel" })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request Sent", response=CancellationResponse.class )  })
    @PostMapping("/cancel")
    public CancellationResponse cancel(@RequestBody CancellationRequest request) {

        CancellationResponse response = campsiteService.cancel(request);

        return response;
    }

    @ApiOperation(value = "Initialize Available Schedule", notes = "Initialize Available Schedule", tags = { "initialize" })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request Sent", response=InitializeResponse.class )  })
    @PostMapping("/init")
    public InitializeResponse sample(@RequestBody InitializeRequest request) {

        InitializeResponse response = campsiteService.initialize(request);

        return response;
    }

}
