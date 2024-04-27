package com.HelloWay.HelloWay.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
@NoArgsConstructor
@Table(name = "baskets")
public class Basket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_basket ;

    @OneToMany()
    List<BasketProduct> basketProducts;

    @JsonIgnore
    @OneToOne
    Command command;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "idTable")
    Board board;

}
