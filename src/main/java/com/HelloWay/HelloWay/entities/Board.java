package com.HelloWay.HelloWay.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.*;
import javax.validation.constraints.Positive;
import java.util.List;

@Data
@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
@NoArgsConstructor
@Table(name = "boards")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTable ;

    //Unique
    @Positive
    @Unique
    private int numTable ;


    @Column(length = 20)
    private boolean availability ;



    @Positive
    private int placeNumber;

    @JsonIgnore
    @OneToMany(mappedBy = "board")
    List<Basket> baskets ;

    @JsonIgnore
    @ManyToOne
    Zone zone ;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id")
    private User user;



    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_reservation")
    private Reservation reservation ;

    public void removeBaskets() {
        if (baskets != null) {
            baskets.clear();
        }
    }


}
