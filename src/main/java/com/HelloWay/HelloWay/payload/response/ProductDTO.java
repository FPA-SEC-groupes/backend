package com.HelloWay.HelloWay.payload.response;

import com.HelloWay.HelloWay.entities.Image;

import java.util.List;

public class ProductDTO {
    private Long idProduct;
    private String productTitle;
    private Float price;
    private String description;
    private Boolean available ;
    private List<Image> images;

    private  boolean hasActivePromotion = false ;
    private int percentage ;

    private long promotionId;

    public ProductDTO(Long idProduct,
                      String productTitle,
                      Float price,
                      String description,
                      Boolean available,
                      List<Image> images,
                      boolean hasActivePromotion,
                      int percentage,
                      int promotionId) {
        this.idProduct = idProduct;
        this.productTitle = productTitle;
        this.price = price;
        this.description = description;
        this.available = available;
        this.images = images;
        this.hasActivePromotion = hasActivePromotion;
        this.percentage = percentage;
        this.promotionId = promotionId;
    }

    public ProductDTO(Long idProduct,
                      String productTitle,
                      Float price,
                      String description,
                      Boolean available,
                      List<Image> images,
                      boolean hasActivePromotion,
                      int percentage) {
        this.idProduct = idProduct;
        this.productTitle = productTitle;
        this.price = price;
        this.description = description;
        this.available = available;
        this.images = images;
        this.hasActivePromotion = hasActivePromotion;
        this.percentage = percentage;
    }

    public ProductDTO() {
    }

    public Long getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(Long idProduct) {
        this.idProduct = idProduct;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public boolean isHasActivePromotion() {
        return hasActivePromotion;
    }

    public void setHasActivePromotion(boolean hasActivePromotion) {
        this.hasActivePromotion = hasActivePromotion;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(long promotionId) {
        this.promotionId = promotionId;
    }
}
