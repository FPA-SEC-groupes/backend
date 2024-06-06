package com.HelloWay.HelloWay.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "restrictions")
@Setter
@Getter
@ToString
@AllArgsConstructor
public class Restrictions implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "reservaions_id")
    private Reservation  reservation;

    public Restrictions() {
    }


    // Getters and setters
}
