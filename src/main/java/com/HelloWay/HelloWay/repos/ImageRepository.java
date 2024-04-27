package com.HelloWay.HelloWay.repos;

import com.HelloWay.HelloWay.entities.Image;
import org.springframework.data.repository.CrudRepository;

public interface ImageRepository extends CrudRepository<Image, String> {
}
