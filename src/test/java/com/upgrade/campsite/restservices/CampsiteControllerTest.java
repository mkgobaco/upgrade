package com.upgrade.campsite.restservices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upgrade.campsite.dto.*;
import com.upgrade.campsite.entities.Reservation;
import com.upgrade.campsite.entities.ReservationsRepository;
import com.upgrade.campsite.entities.ScheduleRepository;
import com.upgrade.campsite.services.CampsiteService;
import com.upgrade.campsite.services.IdService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations="classpath:application-test.properties")
public class CampsiteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReservationsRepository reservationsRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IdService idService;

    @Autowired
    private CampsiteService campsiteService;

    LocalDate availableStartDate = LocalDate.now();
    LocalDate availableEndDate = availableStartDate.plusWeeks(8L);
    Period numOfDays = Period.between(availableStartDate, availableEndDate);

    @BeforeEach
    void beforeEach() {
        scheduleRepository.deleteAll();
        reservationsRepository.deleteAll();
        InitializeRequest initializeRequest = InitializeRequest.builder()
                .availableStartDate(availableStartDate)
                .availableEndDate(availableEndDate)
                .build();
        campsiteService.initialize(initializeRequest);
    }

    @Test
    public void reserve() throws Exception {

        ReservationRequest reservationRequest = ReservationRequest.builder()
                .firstName("Michael")
                .lastName("Jordan")
                .email("michael@jordan.com")
                .checkInDate(LocalDate.of(2020,7,2))
                .checkOutDate(LocalDate.of(2020,7,5))
                .build();

        this.mockMvc.perform(
                   post("/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationRequest))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationRequest.firstName").value("Michael"))
                .andExpect(jsonPath("$.reservationRequest.lastName").value("Jordan"))
                .andExpect(jsonPath("$.reservationRequest.email").value("michael@jordan.com"))
        ;

    }

    @Test
    public void cancel() throws Exception {

        LocalDate.of(2020, 01, 01);

        String bookingId = idService.generateId(5);

        Reservation reservation = Reservation.builder()
                .id(1L)
                .bookingId(bookingId)
                .firstName("M")
                .lastName("J")
                .email("m@j.com")
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now())
                .build();

        reservation = reservationsRepository.save(reservation);

        CancellationRequest cancellationRequest = CancellationRequest.builder()
                .bookingId(bookingId)
                .build();

        this.mockMvc.perform(
                post("/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancellationRequest))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cancellationRequest.bookingId").value(bookingId));

    }

    @Test
    public void modify() throws Exception {

        LocalDate.of(2020, 01, 01);

        String bookingId = idService.generateId(5);

        Reservation reservation = Reservation.builder()
                .id(1L)
                .bookingId(bookingId)
                .firstName("M")
                .lastName("J")
                .email("m@j.com")
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now())
                .build();

        reservation = reservationsRepository.save(reservation);

        ModificationRequest modificationRequest = ModificationRequest.builder()
                .bookingId(bookingId)
                .firstName("M2")
                .lastName("J2")
                .email("m2@j2.com")
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now())
                .build();

        this.mockMvc.perform(
                post("/modify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modificationRequest))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modificationRequest.firstName").value("M2"))
                .andExpect(jsonPath("$.modificationRequest.lastName").value("J2"))
                .andExpect(jsonPath("$.modificationRequest.email").value("m2@j2.com"))
        ;

    }

}