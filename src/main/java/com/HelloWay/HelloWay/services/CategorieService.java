package com.HelloWay.HelloWay.services;

import com.HelloWay.HelloWay.entities.*;
import com.HelloWay.HelloWay.repos.CategorieRepository;
import com.google.zxing.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategorieService {
    @Autowired
    CategorieRepository categorieRepository ;
    @Autowired
    SpaceService spaceService;
    @Autowired
    BoardService boardService;
    @Autowired
    ProductService productService;

    public Optional<Categorie> addCategorie(Categorie categorie) throws Exception{
        if (!categorieRepository.existsByCategoryTitle(categorie.getCategoryTitle()))
        return Optional.of(categorieRepository.save(categorie));
        else throw new IllegalArgumentException("categorie exists");

    }
    public List<Categorie> findAllCategories() {
        return categorieRepository.findAll();
    }

    public Categorie updateCategorie(Categorie updatedCategorie) {
        Categorie existingCategorie = categorieRepository.findById(updatedCategorie.getId_category()).orElse(null);
        if (existingCategorie != null) {
            // Copy the properties from the updatedCategorie to the existingCategorie
            existingCategorie.setCategoryTitle(updatedCategorie.getCategoryTitle());

            categorieRepository.save(existingCategorie);
            return existingCategorie;
        } else {
            // Handle the case where the categorie doesn't exist in the database
            // You may throw an exception or handle it based on your use case.
            return null;
        }
    }
    public Categorie findCategorieById(Long id) {
        return categorieRepository.findById(id)
                .orElse(null);
    }

    //TODO : test this please
    public void deleteCategorie(Long id) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("categorie Not found"));

        List<Product> products = new ArrayList<>(categorie.getProducts());
        for (Product product : products) {
            productService.deleteProduct(product.getIdProduct());
            categorie.getProducts().remove(product);
        }
        categorieRepository.save(categorie);

        categorieRepository.deleteById(id);
    }

    // exist Exeption
    public Categorie addCategorieByIdSpace(Categorie categorie,Long idSpace)  {

        Space space = spaceService.findSpaceById(idSpace);
        List<Categorie> categories = new ArrayList<Categorie>();
        categories = space.getCategories();
        Categorie categorieObject = new Categorie();
        categorieObject = categorie;
        categorieObject.setSpace(space);
        categorieRepository.save(categorieObject);

        categories.add(categorieObject);
        space.setCategories(categories);
        spaceService.updateSpace(space);

            return categorieObject;
    }


    public List<Categorie> getCategoriesByIdSpace(Long idSpace){

        Space space = spaceService.findSpaceById(idSpace);
        return space.getCategories();
    }



    public Boolean categorieExistsByTitle(Categorie categorie){
        return categorieRepository.existsByCategoryTitle(categorie.getCategoryTitle());
    }

    public Boolean categorieExistsByTitleInSpace(Categorie categorie, Long idSpace) {

        Boolean result = false;
        Space space = spaceService.findSpaceById(idSpace);
        List<Categorie> categories = new ArrayList<Categorie>();
        categories = space.getCategories();
        for (Categorie cat : categories) {
            if (cat.getCategoryTitle().equals(categorie.getCategoryTitle())) {
                result = true;
            }
        }
        return result ;
    }

    public Page<Categorie> getCategories(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return categorieRepository.findAll(pageable);
    }


}
