package com.HelloWay.HelloWay.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String fileName;
    private String fileType;
    @Lob
    private byte[] data;

    @JsonIgnore
    @OneToOne(mappedBy = "image")
    User user;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_space")
    private Space space;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_product")
    private Product product;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="idEvent")
    private Event event;
    public Image(Object o, String fileName, String fileType, byte[] data) {
        this.id = o.toString();
        this.fileName = fileName;
        this.fileType = fileType ;
        this.data = data;
    }
}
