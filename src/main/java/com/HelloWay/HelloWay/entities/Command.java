package com.HelloWay.HelloWay.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
@Table(	name = "commands")
@NoArgsConstructor
public class Command {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCommand;

    @Enumerated
    @Column(length = 20)
    private Status status = Status.NOT_YET;

    @Column
    private LocalDateTime timestamp;

    @Column
    private double sum = 0;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_user")
    private User user;

    // the server how will manage this command
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_server", insertable = true, updatable = true)
    private User server;



    @OneToOne(mappedBy = "command")
    private Basket basket;
}
