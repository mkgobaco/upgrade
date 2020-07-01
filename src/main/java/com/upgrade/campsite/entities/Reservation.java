package com.upgrade.campsite.entities;

import com.upgrade.campsite.enums.ReservationStatus;
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
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column
    private String bookingId;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String email;

    @Column
    private LocalDate checkInDate;

    @Column
    private LocalDate checkOutDate;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

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
