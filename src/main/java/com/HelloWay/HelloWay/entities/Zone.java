package com.HelloWay.HelloWay.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
@Table(	name = "zones")
@NoArgsConstructor
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idZone ;

    @NotBlank
    @Column(length = 20)
    private String zoneTitle ;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_space")
    private Space space;

    @OneToMany(mappedBy = "zone")
    List<Board> boards ;

    @JsonIgnore
    @OneToMany(mappedBy = "zone")
    private List<User> servers = new ArrayList<>();
}
