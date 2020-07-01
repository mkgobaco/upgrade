package com.upgrade.campsite.entities;

import com.upgrade.campsite.enums.ReservationStatus;
import com.upgrade.campsite.enums.ScheduleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column
    private String bookingId;

    @Column
    private LocalDate scheduleDate;

    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;

    @Column
    @CreatedDate
    private LocalDateTime createdDate;

    @Column
    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @Column
    @LastModifiedBy
    private String modifiedBy;
}
