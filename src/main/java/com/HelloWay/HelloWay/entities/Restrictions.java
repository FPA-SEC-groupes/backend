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

    private int numberOfRestrictions=1;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Restrictions() {
    }

    public Restrictions(String description, int numberOfRestrictions, User user) {
        this.description = description;
        this.numberOfRestrictions = numberOfRestrictions;
        this.user = user;
    }

    // Getters and setters
}
