package com.HelloWay.HelloWay.entities;

import lombok.*;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "shifts")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Shift implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shiftId;

    @ManyToOne
    @JoinColumn(name = "waiter_id", nullable = false)
    private User waiter;

    private LocalDate shiftDate;
    private LocalTime startTime;
    private LocalTime endTime;
}
