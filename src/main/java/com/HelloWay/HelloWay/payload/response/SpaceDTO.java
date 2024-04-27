package com.HelloWay.HelloWay.payload.response;

import com.HelloWay.HelloWay.entities.Image;
import com.HelloWay.HelloWay.entities.SpaceCategorie;

import java.util.List;

public class SpaceDTO {
    private Long id_space;
    private String titleSpace;
    private String latitude ;
    private String longitude;
    private Long phoneNumber;
    private String description;
    private Float rating;
    private double surfaceEnM2;
    private int numberOfRating;
    private SpaceCategorie spaceCategorie ;
    private List<Image> images;

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
    //TODO : add the image :: Done

    public SpaceDTO(Long id_space,
                    String titleSpace,
                    String latitude,
                    String longitude,
                    Long phoneNumber,
                    String description,
                    Float rating,
                    double surfaceEnM2,
                    int numberOfRating,
                    SpaceCategorie spaceCategorie) {
        this.id_space = id_space;
        this.titleSpace = titleSpace;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.rating = rating;
        this.surfaceEnM2 = surfaceEnM2;
        this.numberOfRating = numberOfRating;
        this.spaceCategorie = spaceCategorie;
    }

    public Long getId_space() {
        return id_space;
    }

    public void setId_space(Long id_space) {
        this.id_space = id_space;
    }

    public String getTitleSpace() {
        return titleSpace;
    }

    public void setTitleSpace(String titleSpace) {
        this.titleSpace = titleSpace;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public double getSurfaceEnM2() {
        return surfaceEnM2;
    }

    public void setSurfaceEnM2(double surfaceEnM2) {
        this.surfaceEnM2 = surfaceEnM2;
    }

    public int getNumberOfRating() {
        return numberOfRating;
    }

    public void setNumberOfRating(int numberOfRating) {
        this.numberOfRating = numberOfRating;
    }

    public SpaceCategorie getSpaceCategorie() {
        return spaceCategorie;
    }

    public void setSpaceCategorie(SpaceCategorie spaceCategorie) {
        this.spaceCategorie = spaceCategorie;
    }

    public SpaceDTO() {
        // Default constructor
    }

}
