package com.upgrade.campsite.entities;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.LockModeType;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ReservationsRepository extends CrudRepository<Reservation, Long> {

    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    Optional<Reservation> findByBookingId(String bookingId);

    @Query("select r from Reservation r where bookingId = ?1 ")
    Optional<Reservation> findByBookingIdNoLock(String bookingId);

    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    Optional<Reservation> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    List<Reservation> findAll();

    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    @Query("select r from Reservation r where checkInDate <= ?1 or checkOutDate >= ?2")
    List<Reservation> findReservationConflicts(LocalDate checkInDate, LocalDate checkOutDate);
}
