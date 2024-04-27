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
import java.util.List;

@Data
@ToString
@Getter
@Setter
@Entity
@Table(name = "categories")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id_category")
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_category;

    @NotBlank
    @Column(length = 20)
    private String categoryTitle ;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_space")
    private Space space;

    @JsonIgnore
    @OneToMany()
    List<Product> products;

}
