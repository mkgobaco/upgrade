package com.upgrade.campsite.entities;

import com.upgrade.campsite.enums.ScheduleStatus;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends CrudRepository<Schedule, Long> {

    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    List<Schedule> findByStatus(ScheduleStatus status);

    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    Optional<Schedule> findByScheduleDate(LocalDate scheduleDate);

    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    Optional<Schedule> findByScheduleDateAndStatus(LocalDate scheduleDate, ScheduleStatus scheduleStatus);

    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    List<Schedule> findByBookingId(String bookingId);

    @Query("select s from Schedule s " +
            "where bookingId = ?1")
    List<Schedule> findByBookingIdNoLock(String bookingId);

    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    @Query("select s from Schedule s " +
            "where scheduleDate >= ?1 and scheduleDate <= ?2 " +
            "and status =  ?3")
    List<Schedule> findByBetweenDatesByStatus(LocalDate startDate,
                                              LocalDate endDate,
                                              ScheduleStatus scheduleStatus);

    List<Schedule> findAll();
}
