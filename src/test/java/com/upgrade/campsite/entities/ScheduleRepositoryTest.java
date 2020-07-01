package com.upgrade.campsite.entities;

import com.upgrade.campsite.enums.ScheduleStatus;
import com.upgrade.campsite.services.IdService;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class ScheduleRepositoryTest {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Test
    @Transactional
    void findAvailableDates() {
        LocalDate startDate = LocalDate.of(2020, 07, 1);
        LocalDate endDate = LocalDate.of(2020, 07, 10);
        List<Schedule> schedules = scheduleRepository
                .findByBetweenDatesByStatus(startDate, endDate, ScheduleStatus.AVAILABLE);
        assertNotNull(schedules);
    }

}