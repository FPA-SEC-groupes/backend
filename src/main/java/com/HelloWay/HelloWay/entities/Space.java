package com.HelloWay.HelloWay.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ToString
@Getter
@Setter
@Entity
@Table(name = "spaces")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id_space")
public class Space {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id_space;

    @NotBlank
    @Column(length = 20)
    private String titleSpace;

    @NotBlank
    private String latitude ;


    @NotBlank
    @Column(length = 40)
    private String longitude;



    private Long phoneNumber;

    @NotNull
    private Long numberOfRate;


    @NotBlank
    @Column(length = 40)
    private String description;


    @Column(length = 40)
    private Float rating;

    @Column(length = 20)
    private double surfaceEnM2;

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getSurfaceEnM2() {
        return surfaceEnM2;
    }

    public void setSurfaceEnM2(double surfaceEnM2) {
        this.surfaceEnM2 = surfaceEnM2;
    }

    public SpaceCategorie getSpaceCategorie() {
        return spaceCategorie;
    }

    public void setSpaceCategorie(SpaceCategorie spaceCategorie) {
        this.spaceCategorie = spaceCategorie;
    }

    @Column(length = 40)
    private int numberOfRating;

    @Column
    private SpaceCategorie spaceCategorie ;

    @OneToMany(mappedBy="space")
    private List<Event> events;

    @OneToMany(mappedBy="space")
    private List<Categorie> categories ;

    @OneToMany(mappedBy="space")
    private List<Zone> zones;

    @OneToMany(mappedBy="space")
    private List<Reservation> reservations;

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

    public int getNumberOfRating() {
        return numberOfRating;
    }

    public void setNumberOfRating(int numberOfRating) {
        this.numberOfRating = numberOfRating;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<Categorie> getCategories() {
        return categories;
    }

    public void setCategories(List<Categorie> categories) {
        this.categories = categories;
    }

    public List<Zone> getZones() {
        return zones;
    }

    public void setZones(List<Zone> zones) {
        this.zones = zones;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public List<Reclamation> getReclamations() {
        return reclamations;
    }

    public void setReclamations(List<Reclamation> reclamations) {
        this.reclamations = reclamations;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<User> getServers() {
        return servers;
    }

    public void setServers(List<User> servers) {
        this.servers = servers;
    }

    public User getModerator() {
        return moderator;
    }

    public void setModerator(User moderator) {
        this.moderator = moderator;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    @OneToMany(mappedBy="space")
    private List<Reclamation> reclamations;

    @JsonIgnore
    @OneToMany(mappedBy="space")
    private List<User> users;

    @JsonIgnore
    @OneToMany(mappedBy="serversSpace")
    private List<User> servers;

    @JsonIgnore
    @OneToOne(mappedBy="moderatorSpace")
    private User moderator;


    @OneToMany(mappedBy="space")
    private List<Image> images;

    @OneToMany(mappedBy="space")
    private List<PrimaryMaterial> primaryMaterials;

    public List<PrimaryMaterial> getPrimaryMaterials() {
        return primaryMaterials;
    }

    public void setPrimaryMaterials(List<PrimaryMaterial> primaryMaterials) {
        this.primaryMaterials = primaryMaterials;
    }

    public Long getNumberOfRate() {
        return numberOfRate;
    }

    public void setNumberOfRate(Long numberOfRate) {
        this.numberOfRate = numberOfRate;
    }
}
