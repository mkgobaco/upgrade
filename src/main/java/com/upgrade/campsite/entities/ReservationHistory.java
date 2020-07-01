package com.upgrade.campsite.entities;

import com.upgrade.campsite.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ReservationHistory {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private Long reservationId;

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

    @Column
    private LocalDateTime createDateTime;

    @Column
    private String createdBy;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

}
