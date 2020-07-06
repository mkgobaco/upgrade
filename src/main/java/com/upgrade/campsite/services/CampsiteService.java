package com.upgrade.campsite.services;

import com.upgrade.campsite.dto.*;
import com.upgrade.campsite.entities.Reservation;
import com.upgrade.campsite.entities.ReservationsRepository;
import com.upgrade.campsite.entities.Schedule;
import com.upgrade.campsite.entities.ScheduleRepository;
import com.upgrade.campsite.enums.ReservationStatus;
import com.upgrade.campsite.enums.ScheduleStatus;
import com.upgrade.campsite.exceptions.CancellationException;
import com.upgrade.campsite.exceptions.ModificationException;
import com.upgrade.campsite.exceptions.ReservationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CampsiteService {

    private IdService idService;

    private ReservationsRepository reservationsRepository;

    private ScheduleRepository scheduleRepository;

    private Integer bookingIdLength;

    private Integer minimumDaysAdvance;

    private Integer maximumDaysAdvance;

    private Integer maximumDays;

    private String modifiedBy;

    @Autowired
    public CampsiteService(IdService idService,
                           ReservationsRepository reservationsRepository,
                           ScheduleRepository scheduleRepository,
                           @Value("${upgrade.campsite.bookingIdLength}") String bookingIdLength,
                           @Value("${upgrade.campsite.minimumDaysAdvance}") Integer minimumDaysAdvance,
                           @Value("${upgrade.campsite.maximumDaysAdvance}") Integer maximumDaysAdvance,
                           @Value("${upgrade.campsite.maximumDays}") Integer maximumDays,
                           @Value("${upgrade.campsite.modifiedBy}") String modifiedBy) {
        this.idService = idService;
        this.reservationsRepository = reservationsRepository;
        this.scheduleRepository = scheduleRepository;
        this.bookingIdLength = Integer.valueOf(bookingIdLength);
        this.minimumDaysAdvance = minimumDaysAdvance;
        this.maximumDaysAdvance = maximumDaysAdvance;
        this.maximumDays = maximumDays;
        this.modifiedBy = modifiedBy;
    }

    @Transactional
    public Optional<Reservation> reservation(String bookingId) {
        return reservationsRepository.findByBookingId(bookingId);
    }

    @Transactional
    public List<Reservation> reservations() {
        return reservationsRepository.findAll();
    }

    @Transactional
    public List<Schedule> schedules() {
        return scheduleRepository.findAll();
    }

    /**
     * @param schedulesRequest
     * @return
     */
    @Transactional
    public SchedulesResponse available(final SchedulesRequest schedulesRequest) {

        LocalDate startDate = schedulesRequest.getStartDate();
        LocalDate endDate = schedulesRequest.getEndDate();

        List<Schedule> schedules =
                scheduleRepository.findByBetweenDatesByStatus(startDate, endDate, ScheduleStatus.AVAILABLE);
        List<LocalDate> availableDates = schedules.stream().map(s -> s.getScheduleDate()).collect(Collectors.toList());
        SchedulesResponse schedulesResponse = SchedulesResponse.builder()
                .availableDates(availableDates)
                .schedulesRequest(schedulesRequest)
                .build();

        return schedulesResponse;
    }

    public Boolean validateMaxDays(LocalDate startDate, LocalDate endDate, Integer maxDays) {
        if (endDate == null) {
            return false;
        }
        return !endDate
                .isAfter(startDate.plusDays(maxDays));
    }

    public Boolean validateMinDays(LocalDate startDate, LocalDate endDate, Integer minDays) {
        if (endDate == null) {
            return false;
        }
        return endDate
                .isAfter(startDate.plusDays(minDays) )
                || endDate.isEqual(startDate.plusDays(minDays));
    }

    public Boolean validateMaxAdvance(LocalDate checkInDate, Integer maxDays) {
        return validateMaxDays(LocalDate.now(), checkInDate, maxDays);
    }

    public Boolean validateMinAdvance(LocalDate checkInDate, Integer minDays) {
        return validateMinDays(LocalDate.now(), checkInDate, minDays);
    }

    /**
     * Returns list of validation errors.
     * @return
     */
    public List<String> validateReservationRequest(ReservationRequest reservationRequest) {
        List<String> errors = new ArrayList<>();

        String firstName = reservationRequest.getFirstName();
        String lastName = reservationRequest.getLastName();
        String email = reservationRequest.getEmail();
        LocalDate checkInDate = reservationRequest.getCheckInDate();
        LocalDate checkOutDate = reservationRequest.getCheckOutDate();

        if (firstName == null || firstName.isEmpty()) {
            errors.add("First Name is required.");
        }
        if (lastName == null || lastName.isEmpty()) {
            errors.add("Last Name is required.");
        }
        if (email == null || email.isEmpty()) {
            errors.add("Email is required.");
        }
        if (checkInDate == null || checkOutDate == null) {
            errors.add("Check In Date and Check Out Date are required.");
        } else {
            if (!validateMaxDays(checkInDate, checkOutDate, maximumDays)) {
                errors.add("Cannot reserve more than 3 days.");
            }
            if (!validateMaxAdvance(checkInDate, maximumDaysAdvance)) {
                errors.add("Cannot reserve more than 30 days in advance.");
            }
            if (!validateMinAdvance(checkInDate, minimumDaysAdvance)) {
                errors.add("Need to reserve at least 1 day in advance.");
            }
            if (!validateMinDays(checkInDate, checkOutDate, 1)) {
                errors.add("Need to reserve at least 1 day.");
            }
        }
        return errors;
    }

    @Transactional
    public ReservationResponse reserve(ReservationRequest reservationRequest) {

        CreateReservationDTO reservationDTO = createReservation(reservationRequest);

        String bookingId = reservationDTO.getReservation().getBookingId();

        CreateSchedulesDTO schedulesDTO = createSchedules(reservationRequest.getCheckInDate(), reservationRequest.getCheckOutDate(), bookingId);

        List<String> errors = reservationDTO.getErrors();
        errors.addAll(schedulesDTO.getErrors());

        if (errors.isEmpty()) {
            reservationsRepository.save(reservationDTO.getReservation());
            scheduleRepository.saveAll(schedulesDTO.getSchedules());
        } else {
            throw new ReservationException(reservationRequest, errors);
        }

        ReservationResponse reservationResponse = ReservationResponse.builder()
                .bookingId(bookingId)
                .reservationRequest(reservationRequest)
                .build();
        return reservationResponse;
    }

    @Transactional
    public CancellationResponse cancel(CancellationRequest cancellationRequest) {

        String bookingId = cancellationRequest.getBookingId();

        Optional<Reservation> reservation = reservationsRepository.findByBookingId(bookingId);

        if (!reservation.isPresent()) {
            throw new CancellationException(cancellationRequest,
                    Arrays.asList("Cannot cancel non-existing BookingID="+bookingId));
        }

        if (reservation.get().getStatus() == ReservationStatus.CANCELED) {
            throw new CancellationException(cancellationRequest,
                    Arrays.asList("Cannot cancel already cancelled BookingID="+bookingId));
        }

        reservation.get().setStatus(ReservationStatus.CANCELED);

        reservationsRepository.save(reservation.get());

        List<Schedule> schedules = scheduleRepository.findByBookingId(bookingId);

        List<LocalDate> cancelledDates = new ArrayList<>();

        for (Schedule schedule: schedules) {
            cancelledDates.add(schedule.getScheduleDate());
            schedule.setBookingId(null);
            schedule.setStatus(ScheduleStatus.AVAILABLE);
            scheduleRepository.save(schedule);
        }

        CancellationResponse cancellationResponse = CancellationResponse.builder()
                .firstName(reservation.get().getFirstName())
                .lastName(reservation.get().getLastName())
                .email(reservation.get().getEmail())
                .checkInDate(reservation.get().getCheckInDate())
                .checkOutDate(reservation.get().getCheckOutDate())
                .cancellationRequest(cancellationRequest)
                .cancelledDates(cancelledDates)
                .build();

        return cancellationResponse;
    }

    @Transactional
    public ModificationResponse modify(ModificationRequest modificationRequest) {

        List<String> errors = new ArrayList<>();

        String bookingId = modificationRequest.getBookingId();
        String firstName = modificationRequest.getFirstName();
        String lastName = modificationRequest.getLastName();
        String email = modificationRequest.getEmail();
        LocalDate checkInDate = modificationRequest.getCheckInDate();
        LocalDate checkOutDate = modificationRequest.getCheckOutDate();


        Optional<Reservation> reservation = reservationsRepository.findByBookingId(bookingId);

        if (
                reservation.get().getFirstName().equals(firstName)
                && reservation.get().getLastName().equals(lastName)
                && reservation.get().getEmail().equals(email)
                && reservation.get().getCheckInDate().equals(checkInDate)
                && reservation.get().getCheckOutDate().equals(checkOutDate)
        ) {
            throw new ModificationException(
                    modificationRequest,
                    Arrays.asList("Modification request is the same as existing bookingId=" + bookingId)
            );
        }

        CancellationResponse cancellationResponse = null;
        try {
            cancellationResponse = cancel(CancellationRequest.builder().bookingId(bookingId).build());
        } catch (CancellationException ex) {
            throw new ModificationException(modificationRequest, ex.getErrors());
        }

        if (firstName == null || firstName.isEmpty()) {
            firstName = cancellationResponse.getFirstName();
        }

        if (lastName == null || lastName.isEmpty()) {
            lastName = cancellationResponse.getLastName();
        }

        if (checkInDate == null) {
            checkInDate = cancellationResponse.getCheckInDate();
        }

        if (checkOutDate == null) {
            checkOutDate = cancellationResponse.getCheckOutDate();
        }

        ReservationRequest reservationRequest = ReservationRequest.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .checkInDate(checkInDate)
                .checkOutDate(checkOutDate)
                .build();

        ReservationResponse reservationResponse = null;
        try {
            reservationResponse = reserve(reservationRequest);
        } catch (ReservationException ex) {
            throw new ModificationException(modificationRequest, ex.getErrors());
        }

        ModificationResponse modificationResponse = ModificationResponse.builder()
                .modificationRequest(modificationRequest)
                .cancellationResponse(cancellationResponse)
                .reservationResponse(reservationResponse)
                .errors(errors)
                .build();

        return modificationResponse;
    }

    /**
     * This method is for initializing the SCHEDULES table by inserting AVAILABLE dates
     * into the table between the given dates.
     * @param request
     * @return
     */
    @Transactional
    public InitializeResponse initialize(InitializeRequest request) {
        LocalDate availableStartDate = request.getAvailableStartDate();
        LocalDate availableEndDate = request.getAvailableEndDate();

        for (LocalDate ii = availableStartDate;
             ii.isBefore(availableEndDate) || ii.isEqual(availableEndDate);
             ii = ii.plusDays(1L)
        ) {
            Optional<Schedule> scheduleDate = scheduleRepository.findByScheduleDate(ii);
            if (!scheduleDate.isPresent()) {
                scheduleDate = Optional.of(Schedule.builder().scheduleDate(ii).build());
                scheduleDate.get().setStatus(ScheduleStatus.AVAILABLE);
                scheduleDate.get().setBookingId(null);
                scheduleDate.get().setModifiedBy(modifiedBy);
                scheduleRepository.save(scheduleDate.get());
            }
        }

        InitializeResponse response = InitializeResponse.builder().build();

        return response;
    }

    /**
     * This version of reserve() is for concurrency testing only.
     * @param reservationRequest
     * @param sleepInMilliseconds
     * @return
     * @throws InterruptedException
     */
    @Transactional
    public ReservationResponse slowReserve(ReservationRequest reservationRequest,
                                           Long sleepInMilliseconds) throws InterruptedException {

        CreateReservationDTO reservationDTO = createReservation(reservationRequest);

        String bookingId = reservationDTO.getReservation().getBookingId();

        log.info("Booking ID:" + bookingId + " After creating Reservation");


        log.info("Booking ID:" + bookingId + " Before 1st Thread sleep " + sleepInMilliseconds);

        Thread.sleep(sleepInMilliseconds);

        log.info("Booking ID:" + bookingId + " After 1st Thread sleep " + sleepInMilliseconds);


        log.info("Booking ID:" + bookingId + " Before creating Schedules");

        CreateSchedulesDTO schedulesDTO = createSchedules(reservationRequest.getCheckInDate(), reservationRequest.getCheckOutDate(), bookingId);

        List<String> errors = reservationDTO.getErrors();
        errors.addAll(schedulesDTO.getErrors());

        log.info("Booking ID:" + bookingId + " After creating Schedules");

        log.info("Booking ID:" + bookingId + " Before 2nd Thread sleep " + sleepInMilliseconds);

        Thread.sleep(sleepInMilliseconds);

        log.info("Booking ID:" + bookingId + " After 2nd Thread sleep " + sleepInMilliseconds);

        if (errors.isEmpty()) {
            reservationsRepository.save(reservationDTO.getReservation());
            scheduleRepository.saveAll(schedulesDTO.getSchedules());
        } else {
            throw new ReservationException(reservationRequest, errors);
        }

        ReservationResponse reservationResponse = ReservationResponse.builder()
                .bookingId(bookingId)
                .reservationRequest(reservationRequest)
                .build();

        log.info("Booking ID:" + bookingId + " Exiting slowReserve ");

        return reservationResponse;
    }

    private CreateReservationDTO createReservation(ReservationRequest reservationRequest) {

        String bookingId = idService.generateId(bookingIdLength);

        LocalDate checkInDate = reservationRequest.getCheckInDate();
        LocalDate checkOutDate = reservationRequest.getCheckOutDate();

        List<String> errors = validateReservationRequest(reservationRequest);

        Reservation reservation = Reservation.builder()
                .bookingId(bookingId)
                .firstName(reservationRequest.getFirstName())
                .lastName(reservationRequest.getLastName())
                .email(reservationRequest.getEmail())
                .checkInDate(checkInDate)
                .checkOutDate(checkOutDate)
                .status(ReservationStatus.RESERVED)
                .modifiedBy(modifiedBy)
                .build();

        return CreateReservationDTO.builder().reservation(reservation).errors(errors).build();
    }

    private CreateSchedulesDTO createSchedules(LocalDate checkInDate, LocalDate checkOutDate, String bookingId) {

        List<String> errors = new ArrayList<>();

        List<Schedule> schedules = new ArrayList<>();
        for (LocalDate ii = checkInDate;
             ii.isBefore(checkOutDate);
             ii = ii.plusDays(1L))
        {
            Optional<Schedule> schedule = scheduleRepository.findByScheduleDate(ii);
            if (schedule.isPresent()) {
                    if (schedule.get().getStatus().equals(ScheduleStatus.AVAILABLE)) {
                        schedule.get().setStatus(ScheduleStatus.NOT_AVAILABLE);
                        schedule.get().setBookingId(bookingId);
                        schedule.get().setModifiedBy(modifiedBy);
                        schedules.add(schedule.get());
                    } else {
                        errors.add( ii + " Date not available.  " + "Existing Booking ID: " + schedule.get().getBookingId());
                    }
            } else {
                errors.add( ii + " Date not available.  ");
            }
        }

        return CreateSchedulesDTO.builder().schedules(schedules).errors(errors).build();
    }

}
