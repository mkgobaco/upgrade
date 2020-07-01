package com.upgrade.campsite.services;

import com.upgrade.campsite.dto.InitializeRequest;
import com.upgrade.campsite.dto.ReservationRequest;
import com.upgrade.campsite.dto.ReservationResponse;
import com.upgrade.campsite.entities.Reservation;
import com.upgrade.campsite.entities.ReservationsRepository;
import com.upgrade.campsite.entities.Schedule;
import com.upgrade.campsite.entities.ScheduleRepository;
import com.upgrade.campsite.exceptions.CampsiteException;
import com.upgrade.campsite.exceptions.ReservationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.TransactionSystemException;

import javax.persistence.EntityManager;
import javax.persistence.PessimisticLockException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class CampsiteServiceTest {

    @Autowired
    private CampsiteService campsiteService;

    @Autowired
    private ReservationsRepository reservationsRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private EntityManager entityManager;

    LocalDate availableStartDate = LocalDate.now();
    LocalDate availableEndDate = availableStartDate.plusMonths(2L);

    @BeforeEach
    void beforeAll() {

        InitializeRequest initializeRequest = InitializeRequest.builder()
                .availableStartDate(availableStartDate)
                .availableEndDate(availableEndDate)
                .build();
        campsiteService.initialize(initializeRequest);
    }

    @Test
    @Transactional
    void initialize() {
        Optional<Schedule> schedule = scheduleRepository.findByScheduleDate(availableEndDate);
        assertEquals(true, schedule.isPresent());
    }

    @Test
    void testSynchronous() {

        LocalDate startDate = availableStartDate.plusDays(10);
        LocalDate endDate = startDate.plusDays(3L);

        ReservationRequest reservationRequest1 = ReservationRequest.builder()
                .firstName("f1")
                .lastName("l1")
                .email("f1@l1.com")
                .checkInDate(startDate)
                .checkOutDate(endDate)
                .build();
        ReservationRequest reservationRequest2= ReservationRequest.builder()
                .firstName("f2")
                .lastName("l2")
                .email("f2@l2.com")
                .checkInDate(startDate)
                .checkOutDate(endDate)
                .build();
        campsiteService.reserve(reservationRequest1);
        assertThrows(ReservationException.class,  () -> campsiteService.reserve(reservationRequest2));
    }

    /** This test the pessimistic locking to ensure no more than one
     *  reservation requests can reserve the same schedules at the same time.
     *
     *  In the event pessimistic locking does not timeout, optimistic locking features
     *  will take prevent updating of records that have been updated by other
     *  transactions.  (Not in this test.)
     *
     * @throws InterruptedException
     */
    @Test
    void testAsynchronous() throws InterruptedException, ExecutionException {

        entityManager.setProperty("javax.persistence.lock.timeout", 1);

        LocalDate startDate = LocalDate.now().plusDays(5L);
        LocalDate endDate = startDate.plusDays(3L);

        ReservationRequest reservationRequest1 = ReservationRequest.builder()
                .firstName("f1")
                .lastName("l1")
                .email("f1@l1.com")
                .checkInDate(startDate)
                .checkOutDate(endDate)
                .build();
        ReservationRequest reservationRequest2= ReservationRequest.builder()
                .firstName("f2")
                .lastName("l2")
                .email("f2@l2.com")
                .checkInDate(startDate)
                .checkOutDate(endDate)
                .build();

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        List<CompletableFuture<ReservationResponse>> futures = new ArrayList<CompletableFuture<ReservationResponse>>();

        CompletableFuture<ReservationResponse> future1 = CompletableFuture.supplyAsync(
                () -> {
                    ReservationResponse reservationResponse = null;
                    try {
                        reservationResponse = campsiteService.slowReserve(reservationRequest1, 5000L);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    return reservationResponse;
                }, executorService
        );

        futures.add(future1);

        Thread.sleep(1000);

        CompletableFuture<ReservationResponse> future2 = CompletableFuture.supplyAsync(
                () -> {
                    ReservationResponse reservationResponse = null;
                    try {
                        assertThrows(TransactionSystemException.class, () -> {
                            campsiteService.slowReserve(reservationRequest2, 5000L);
                        });
                        ;
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    return reservationResponse;
                }, executorService
        );

        futures.add(future2);

        for (CompletableFuture<ReservationResponse> f: futures) {
            f.join();
        }

        String bookingId = future1.get().getBookingId();
        Optional<Reservation> reservation1 = reservationsRepository.findByBookingIdNoLock(bookingId);
        assertTrue(reservation1.isPresent());

        List<Schedule> schedules = scheduleRepository.findByBookingIdNoLock(bookingId);
        assertNotEquals(0, schedules.size());

        assertNull(future2.get());

        log.info("end");
    }

    @Test
    void validateMaxDays() {
        LocalDate checkInDate = LocalDate.of(2020, 01, 01);
        LocalDate checkOutDate = LocalDate.of(2020, 01, 04);
        assertTrue(campsiteService.validateMaxDays(checkInDate, checkOutDate, 3));
        assertFalse(campsiteService.validateMaxDays(checkInDate, checkOutDate, 2));
    }

    @Test
    void validateMaxAdvance() {
        assertTrue(campsiteService.validateMaxAdvance(LocalDate.now().plusDays(30), 30));
        assertFalse(campsiteService.validateMaxAdvance(LocalDate.now().plusDays(31), 30));
    }

    @Test
    void validateMinDays() {

        LocalDate checkInDate = LocalDate.of(2020, 01, 01);
        LocalDate checkOutDate = LocalDate.of(2020, 01, 04);
        assertTrue(campsiteService.validateMinDays(checkInDate, checkOutDate, 1));
        assertTrue(campsiteService.validateMinDays(checkInDate, checkOutDate, 2));
        assertTrue(campsiteService.validateMinDays(checkInDate, checkOutDate, 3));
        assertFalse(campsiteService.validateMinDays(checkInDate, checkOutDate, 4));
    }

    @Test
    void validateMinAdvance() {

        assertTrue(campsiteService.validateMinAdvance(LocalDate.now().plusDays(1), 1));
        assertFalse(campsiteService.validateMinAdvance(LocalDate.now().plusDays(0), 1));
    }
}