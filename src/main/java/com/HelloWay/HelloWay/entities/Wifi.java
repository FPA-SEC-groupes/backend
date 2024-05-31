package com.HelloWay.HelloWay.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@ToString
@Getter
@Setter
@Entity
@Table(name = "wifis")
public class Wifi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(length = 40)
    private String ssid;  // WiFi network name

    @NotBlank
    @Column(length = 40)
    private String password;


    @ManyToOne(fetch = FetchType.LAZY) // Added FetchType.LAZY for optimization
    @JoinColumn(name = "space_id")
    private Space space;

}
