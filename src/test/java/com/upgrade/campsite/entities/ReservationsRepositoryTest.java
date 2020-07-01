package com.upgrade.campsite.entities;

import com.upgrade.campsite.dto.ReservationResponse;
import com.upgrade.campsite.enums.ReservationStatus;
import com.upgrade.campsite.services.IdService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.TestPropertySource;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
public class ReservationsRepositoryTest {

    @Autowired
    private ReservationsRepository reservationsRepository;

    @Autowired
    private IdService idService;

    @Test
    @Transactional
    public void insert() {
        String bookingId = idService.generateId(5);
        Reservation reservation = Reservation.builder()
                .bookingId(bookingId)
                .firstName("Michael")
                .lastName("Jordan")
                .email("michael@jordan.com")
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now())
                .status(ReservationStatus.RESERVED)
                .build();

        reservationsRepository.save(reservation);

        reservation = reservationsRepository.findByBookingId(bookingId).get();
        assertEquals("Michael", reservation.getFirstName());
    }

    @Test
    @Transactional
    public void findReservationConflicts()
    {
        List<Reservation> reservationList =
        reservationsRepository.findReservationConflicts(LocalDate.now(),  LocalDate.now());
        assertEquals(0, reservationList.size());
    }

}