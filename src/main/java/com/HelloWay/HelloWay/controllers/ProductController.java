package com.HelloWay.HelloWay.controllers;

import com.HelloWay.HelloWay.config.FileUploadUtil;
import com.HelloWay.HelloWay.entities.*;
import com.HelloWay.HelloWay.payload.response.MessageResponse;
import com.HelloWay.HelloWay.payload.response.ProductDTO;
import com.HelloWay.HelloWay.payload.response.ProductOrderUpdate;
import com.HelloWay.HelloWay.repos.ImageRepository;
import com.HelloWay.HelloWay.services.BasketProductService;
import com.HelloWay.HelloWay.services.BasketService;
import com.HelloWay.HelloWay.services.ProductService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    ProductService productService;
    ImageRepository imageRepository;

    BasketService   basketService;
    BasketProductService basketProductService;

    private ModelMapper modelMapper;

    @Autowired
    public ProductController(ProductService productService, ImageRepository imageRepository,
                             BasketService   basketService, BasketProductService basketProductService,
                             ModelMapper modelMapper) {
        this.productService = productService;
        this.imageRepository = imageRepository;
        this.basketService = basketService;
        this.basketProductService = basketProductService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ResponseBody
    public Optional<Product> addNewProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @JsonIgnore
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ResponseBody
    public List<Product> allProducts() {
        return productService.findAllProducts();
    }


    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('PROVIDER','WAITER', 'USER', 'GUEST')")
    @ResponseBody
    public Product findProductById(@PathVariable("id") long id) {
        return productService.findProductById(id);
    }


    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('PROVIDER','WAITER')")
    @ResponseBody
    public Product updateProduct(@RequestBody Product product) {
       return productService.updateProduct(product);
    }

    @PutMapping("/update/{productId}/{percentage}")
    @PreAuthorize("hasAnyRole('PROVIDER','WAITER')")
    @ResponseBody
    public ResponseEntity<?> updateProduct(@RequestBody Product product, @PathVariable long productId, @PathVariable Long percentage) {
        // Find the existing product by ID
        Product existingProduct = productService.findProductById(productId);
        if (existingProduct == null) {
            return ResponseEntity.badRequest().body("Product not found");
        }
        
        // Check for duplicate title in the same category
        Categorie categorie = existingProduct.getCategorie();
        List<Product> categorieProducts = categorie.getProducts();
        for (Product p : categorieProducts) {
            if (!p.getIdProduct().equals(productId) && p.getProductTitle().equals(product.getProductTitle())) {
                return ResponseEntity.badRequest().body("Product exists with this title, please try another");
            }
        }

        // Update product properties
        existingProduct.setProductTitle(product.getProductTitle());
        existingProduct.setPrice((float) (product.getPrice() * (1 + percentage / 100.0)));
        existingProduct.setDescription(product.getDescription());
        existingProduct.setAvailable(product.getAvailable());

        // Save updated product
        productService.updateProduct(existingProduct);

        // Console message to show the updated product
        System.out.println("Updated Product: " + existingProduct);

        return ResponseEntity.ok().body(existingProduct);
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('PROVIDER','WAITER')")
    @ResponseBody
    public void deleteProduct(@PathVariable("id") long id) {
        productService.deleteProduct(id);
    }

    @PostMapping("/add/id_categorie/{id_categorie}/{percentage}")
    @ResponseBody
    public ResponseEntity<?> addNewProductByIdCategorie(@RequestBody Product product, @PathVariable Long id_categorie,@PathVariable Long percentage) {
        if (productService.productExistsByTitleInCategorie(product, id_categorie)){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Product title is already taken! in this Categorie"));
        }else
        {
            
            Product productObject =  productService.addProductByIdCategorie(product, id_categorie,percentage);
            return ResponseEntity.ok().body(productObject);
        }
    }

    @GetMapping("/all/id_categorie/{id_categorie}")
    @PreAuthorize("hasAnyRole('PROVIDER','WAITER', 'USER', 'GUEST')")
    @ResponseBody
    public List<Product> getProductsByIDCategory(@PathVariable Long id_categorie) {
        return productService.getProductsByIdCategorie(id_categorie);
    }

//TODO :: add idPromotion in the true case
    @GetMapping("/all/dto/id_categorie/{id_categorie}")
    @PreAuthorize("hasAnyRole('PROVIDER','WAITER', 'USER', 'GUEST')")
    @ResponseBody
    public ResponseEntity<?> getProductsDtoByIDCategory(@PathVariable Long id_categorie) {
        List<Product> products =  productService.getProductsByIdCategorie(id_categorie);
        if (products.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        List<ProductDTO> productsDto = new ArrayList<>();
        for (Product product : products){
            productsDto.add(modelMapper.map(product, ProductDTO.class));
        }

        for (Product product : products){
        for (Promotion promotion : product.getPromotions()) {
            if (promotion.getStartDate().isBefore(LocalDateTime.now())
                    && promotion.getEndDate().isAfter(LocalDateTime.now())) {
                productsDto.get(products.indexOf(product)).setHasActivePromotion(true);
                productsDto.get(products.indexOf(product)).setPercentage(promotion.getPercentage());
                productsDto.get(products.indexOf(product)).setPromotionId(promotion.getIdEvent());
            }
          }
        }
        return ResponseEntity.ok().body(productsDto);
    }

    @PostMapping("/{id}/images")
    @PreAuthorize("hasAnyRole('PROVIDER','WAITER')")
    public ResponseEntity<String> addImage(@PathVariable("id") Long id,
                                        @RequestParam("file") MultipartFile file) {
        try {
            Product product = productService.findProductById(id);
            if (product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
            }

            // Create the Image entity and set the reference to the Product entity
            Image image = new Image();
            String orgFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String ext = orgFileName.substring(orgFileName.lastIndexOf("."));
            String uploadDir = "photos/product/";
            String fileName = file.getOriginalFilename() + "_" + product.getIdProduct() + ext;
            FileUploadUtil.saveFile(uploadDir, fileName, file);

            image.setProduct(product);
            image.setFileName(fileName);
            image.setFileType(file.getContentType());
            
            // Persist the Image entity to the database
            imageRepository.save(image);

            return ResponseEntity.ok().body("Image uploaded successfully");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
        }
    }
    @DeleteMapping("{idImage}/images/{idSpace}")
    @PreAuthorize("hasAnyRole('PROVIDER','WAITER')")
    public ResponseEntity<?> deleteImage(@PathVariable String idImage, @PathVariable Long idSpace){
        Image image = imageRepository.findById(idImage).orElse(null);
        if (image == null){
            return ResponseEntity.notFound().build();
        }
        Product product = productService.findProductById(idSpace);
        if (product == null){
            return ResponseEntity.notFound().build();
        }
        product.getImages().remove(image);
        productService.updateProduct(product);
        imageRepository.delete(image);
        return ResponseEntity.ok("image deleted successfully for the product");
    }
    @PostMapping("/add/productToBasket/{id_basket}")
    @PreAuthorize("hasAnyRole('USER', 'GUEST')")
    @ResponseBody
    public void addProductToBasket(@RequestBody Product product, @PathVariable Long id_basket, int quantity) {
        Basket basket = basketService.findBasketById(id_basket);
         basketProductService.addProductToBasket(basket, product, quantity);
    }

    @PostMapping("/add/productToBasket/{id_basket}/{id_product}")
    @PreAuthorize("hasAnyRole('USER', 'GUEST')")
    @ResponseBody
    public void addProductToBasketByIds(@PathVariable Long id_basket, int quantity, @PathVariable Long id_product) {
        Basket basket = basketService.findBasketById(id_basket);
        Product product = productService.findProductById(id_product);
        Product Product = new Product();
        Product.setIdProduct(product.getIdProduct());
        Product.setProductTitle(product.getProductTitle());
        Product.setPrice(product.getPrice());
        Product.setDescription(product.getDescription());
        Product.setAvailable(product.getAvailable());
        Product.setOrderIndex(product.getOrderIndex());

        basketProductService.addProductToBasket(basket, Product, quantity);
    }
    @DeleteMapping("/deleteProductFromBasket/{id_product}/{id_basket}")
    @PreAuthorize("hasAnyRole('USER', 'GUEST')")
    @ResponseBody
    public void deleteProductFromBasket(@PathVariable("id_product") long id_product, @PathVariable("id_basket") Long id_basket) {
        basketProductService.deleteProductFromBasket(id_basket,id_product);
    }

    @GetMapping("/all/paging")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public Page<Product> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return productService.getProducts(page, size);
    }

    @PutMapping("/updateOrder")
    @PreAuthorize("hasAnyRole('PROVIDER','WAITER', 'USER', 'GUEST')")
    public ResponseEntity<?> updateProductOrder(@RequestBody List<ProductOrderUpdate> productOrderUpdates) {
        try {
            productService.updateProductOrder(productOrderUpdates);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update product order");
        }
    }

}
