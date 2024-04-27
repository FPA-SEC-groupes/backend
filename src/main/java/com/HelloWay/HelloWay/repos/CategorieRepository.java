package com.HelloWay.HelloWay.repos;

import com.HelloWay.HelloWay.entities.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {

    Boolean existsByCategoryTitle(String categorieTitle);

}
