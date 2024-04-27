package com.HelloWay.HelloWay.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@ToString
@Getter
@Setter
@Entity
@Table(name = "reclamations")
@NoArgsConstructor
public class Reclamation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCommand;

    @NotBlank
    @Column(length = 20)
    private String subject;

    @NotBlank
    @Column(length = 20)
    private LocalDate dateOfIncident;

    @NotBlank
    @Column(length = 20)
    private String description;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_space")
    private Space space;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id")
    private User user;


}
