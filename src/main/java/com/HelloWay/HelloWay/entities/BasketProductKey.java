package com.HelloWay.HelloWay.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasketProductKey implements Serializable {
    @Column(name = "id_basket")
    private Long idBasket ;
    @Column(name = "id_product")
    private Long idProduct;
}
