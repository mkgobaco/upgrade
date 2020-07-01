package com.upgrade.campsite.restservices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upgrade.campsite.dto.ReservationRequest;
import com.upgrade.campsite.dto.ReservationResponse;
import com.upgrade.campsite.entities.Reservation;
import com.upgrade.campsite.entities.ReservationsRepository;
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
    private ObjectMapper objectMapper;

    @BeforeEach
    public void before() {

        LocalDate.of(2020, 01, 01);

        reservationsRepository.save(Reservation.builder()
                .id(1L)
                .firstName("M")
                .lastName("J")
                .email("m@j.com")
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now())
                .build());
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
                .andExpect(jsonPath("$.reservationRequest.firstName").value("Michael"));

    }
}