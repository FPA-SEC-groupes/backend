package com.HelloWay.HelloWay.services;


import com.HelloWay.HelloWay.entities.Basket;
import com.HelloWay.HelloWay.entities.BasketProduct;
import com.HelloWay.HelloWay.entities.BasketProductKey;
import com.HelloWay.HelloWay.entities.Product;
import com.HelloWay.HelloWay.entities.ProductStatus;
import com.HelloWay.HelloWay.entities.Product;
import com.HelloWay.HelloWay.payload.response.QuantitysProduct;
import com.HelloWay.HelloWay.repos.BasketProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.HelloWay.HelloWay.entities.Status.CONFIRMED;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BasketProductService {
    @Autowired
    BasketProductRepository basketProductRepository;
    @Autowired
    BasketService basketService ;
    @Autowired
    ProductService productService ;
    public List<BasketProduct> findAllBasketProducts() {
        return basketProductRepository.findAll();
    }

    public BasketProduct updateBasketProduct(BasketProduct basketProduct) {
        return basketProductRepository.save(basketProduct);
    }

    public BasketProduct findBasketProductById(Long id) {
        return basketProductRepository.findById(id)
                .orElse(null);
    }

    public void deleteBasketProduct(Long id) {
        basketProductRepository.deleteById(id);
    }

    public Optional<BasketProduct> findBasketProductByBasketAndProduct(Basket basket, Product product) {
        return basketProductRepository.findByBasketAndProduct(basket, product);
    }
    //TODO : add the attribute of oldQuantity :: Done
    public void addProductToBasket(Basket basket, Product product, int quantity) {
        // Retrieve products already in the basket
        List<Product> products = getProductsByBasketId(basket.getId_basket());
        
        // Check if the product already exists in the basket
        if (products.contains(product)) {
            List<BasketProduct> basketProducts = getBasketProductsByBasketId(basket.getId_basket());
            for (BasketProduct basketProduct : basketProducts) {
                if (basketProduct.getProduct().equals(product)) {
                    // Update the quantity of the existing product
                    // if(basketProduct.getStatus().equals(ProductStatus.CONFIRMED)){
                    //     basketProduct.setStatus(ProductStatus.NEW);
                    // }
                    // basketProduct.setOldQuantity(basketProduct.getQuantity());
                    basketProduct.setQuantity(basketProduct.getQuantity() + quantity);
                    // basketProduct.setStatus(ProductStatus.NEW); 
                    basketProductRepository.save(basketProduct);
                    return; // Exit after updating the existing product
                }
            }
        } else {
            // Add a new product to the basket
            BasketProduct newBasketProduct = new BasketProduct(
                    new BasketProductKey(basket.getId_basket(), product.getIdProduct()),
                    basket,
                    product,
                    quantity, // Current quantity
                    0 // Old quantity is set to 0
            );
            newBasketProduct.setStatus(ProductStatus.NEW); 
            basketProductRepository.save(newBasketProduct);
        }
    }
    


    public List<BasketProduct> getBasketProductsByBasketId(Long id) {
        Basket basket = basketService.findBasketById(id);
        return new ArrayList<>(basketProductRepository.findAllByBasket(basket));
    }

    public List<BasketProduct> getBasketProductsByProductId(Long id) {
        Product product = productService.findProductById(id);
        return new ArrayList<>(basketProductRepository.findAllByProduct(product));
    }

    //TODO : add the old quantity :: we dont need to update this method :: Done
    public void deleteProductFromBasket(Long bid, Long pid) {
        BasketProduct basketProduct =basketProductRepository.findById_IdBasketAndId_IdProduct(bid, pid);
        if (basketProduct.getQuantity() != 1){
            basketProduct.setQuantity(basketProduct.getQuantity() - 1);
            basketProductRepository.save(basketProduct);
        }else {
        basketProductRepository.delete(basketProduct);
        }
    }


    public void deleteProductFromBasketV2(Long bid, Long pid) {
        BasketProduct basketProduct =basketProductRepository.findById_IdBasketAndId_IdProduct(bid, pid);

            basketProductRepository.delete(basketProduct);
    }


    public List<Product> getProductsByBasketId(Long id) {
        Basket basket = basketService.findBasketById(id);
        List<BasketProduct> basketProducts = new ArrayList<>();
               basketProducts =  basketProductRepository.findAllByBasket(basket);
        List<Product> products = new ArrayList<>();
        for (BasketProduct basketProduct : basketProducts){
            products.add(basketProduct.getProduct());
        }
        return products;
    }

    public Map<Product, QuantitysProduct> getProductsQuantityByBasketId(Long id) {
        Basket basket = basketService.findBasketById(id);
        List<BasketProduct> basketProducts = new ArrayList<>();
        basketProducts =  basketProductRepository.findAllByBasket(basket);
        Map<Product,QuantitysProduct> products_Quantity = new HashMap<>();
        for (BasketProduct basketProduct : basketProducts){
            QuantitysProduct quantitysProduct = new QuantitysProduct(basketProduct.getOldQuantity(),basketProduct.getQuantity());
            products_Quantity.put(basketProduct.getProduct(), quantitysProduct);
        }
        return products_Quantity;
    }


}
