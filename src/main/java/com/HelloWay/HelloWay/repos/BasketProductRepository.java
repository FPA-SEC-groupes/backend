package com.HelloWay.HelloWay.repos;

import com.HelloWay.HelloWay.entities.Basket;
import com.HelloWay.HelloWay.entities.BasketProduct;
import com.HelloWay.HelloWay.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BasketProductRepository extends JpaRepository<BasketProduct, Long> {


    List<BasketProduct> findAllByProduct(Product product);

    List<BasketProduct> findAllByBasket(Basket basket);

    BasketProduct findBasketProductByProduct_idProduct(Long id);

    BasketProduct findById_IdBasketAndId_IdProduct(Long idBasket, Long idProduct);

    void deleteAllBasketProductByProduct(Product product);
    void deleteAllBasketProductByBasket(Basket basket);




}
