package com.HelloWay.HelloWay.services;

import com.HelloWay.HelloWay.entities.BasketProduct;
import com.HelloWay.HelloWay.entities.Categorie;
import com.HelloWay.HelloWay.entities.Product;
import com.HelloWay.HelloWay.entities.Space;
import com.HelloWay.HelloWay.payload.response.ProductOrderUpdate;
import com.HelloWay.HelloWay.repos.BasketProductRepository;
import com.HelloWay.HelloWay.repos.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository ;

    @Autowired
    CategorieService categorieService ;

    @Autowired
    BasketProductRepository basketProductRepository;

    public Optional<Product> addProduct(Product product){

        if (!productRepository.existsByProductTitle(product.getProductTitle()))
            return Optional.of(productRepository.save(product));
        else throw new IllegalArgumentException("products exists with this title");
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public Product updateProduct(Product updatedProduct) {
        Product existingProduct = productRepository.findById(updatedProduct.getIdProduct()).orElse(null);
        if (existingProduct != null) {
            existingProduct.setProductTitle(updatedProduct.getProductTitle());
            existingProduct.setPrice(updatedProduct.getPrice());
            existingProduct.setDescription(updatedProduct.getDescription());
            existingProduct.setAvailable(updatedProduct.getAvailable());
            existingProduct.setCategorie(updatedProduct.getCategorie());
            
            // Save updated product
            return productRepository.save(existingProduct);
        } else {
            // Handle the case where the product doesn't exist in the database
            throw new RuntimeException("Product not found");
        }
    }
    
    public Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElse(null);
    }


    @Transactional
    public void deleteProduct(Long productId) {
        // Retrieve the product by ID
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + productId + " not found."));

        basketProductRepository.deleteAllBasketProductByProduct(product);


        // Clear the basketProducts list to trigger cascading delete
        product.getBasketProducts().clear();
        product.removeCategorie(); // This will remove the association between the product and its categorie


        // Save the changes to update the associations
        productRepository.save(product);

        // Delete the product after disassociating it from baskets
        productRepository.deleteById(productId);
    }



    // exist exeption
    // generation du code table auto increment
    public Product addProductByIdCategorie(Product product, Long id_categorie, Long percentage) {
        product.setPrice((float) (product.getPrice() * (1 + percentage / 100.0)));
        Categorie categorie = categorieService.findCategorieById(id_categorie);
        product.setCategorie(categorie);
        List<Product> products = categorie.getProducts();
        products.add(product);
        Product savedProduct = productRepository.save(product);
        System.out.println("Saved Product: " + savedProduct);
        categorie.setProducts(products);
        return savedProduct;
    }
    
    public List<Product> getProductsByIdCategorie(Long id_categorie){
        Categorie categorie = categorieService.findCategorieById(id_categorie);
        return categorie.getProducts();
    }
    public Boolean productExistsByTitleInCategorie(Product product, Long idCategorie) {

        Boolean result = false;
        Categorie categorie = categorieService.findCategorieById(idCategorie);
        List<Product> products = new ArrayList<Product>();
        products = categorie.getProducts();
        for (Product prod : products) {
            if (prod.getProductTitle().equals(product.getProductTitle())) {
                result = true;
            }
        }
        return result ;
    }

    public Page<Product> getProducts(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return productRepository.findAll(pageable);
    }
    
    // @Transactional
    // public void updateProductOrder(List<Long> productIds) {
    //     for (int i = 0; i < productIds.size(); i++) {
    //         Long productId = productIds.get(i);
    //         Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
    //         product.setOrderIndex(i); // Assume you have an 'orderIndex' field in your Product entity
    //         productRepository.save(product);
    //     }
    // }
    
    public void updateProductOrder(List<ProductOrderUpdate> productOrderUpdates) {
        for (ProductOrderUpdate update : productOrderUpdates) {
            Product product = productRepository.findById(update.getIdProduct())
                                               .orElseThrow(() -> new RuntimeException("Product not found"));
            product.setOrderIndex(update.getOrderIndex());
            productRepository.save(product);
        }
    }
}
