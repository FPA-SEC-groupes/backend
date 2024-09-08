package com.HelloWay.HelloWay.entities;

import lombok.*;
import javax.persistence.*;
import java.util.Date;

@Data
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Demande")
public class Demande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom", nullable = false)
    private String nom;


    @Column(name = "prenom", nullable = false)
    private String prenom;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "restaurant", nullable = false)
    private String restaurant;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date(); // Automatically set to current date and time

    // You can add additional relationships here if needed

}
