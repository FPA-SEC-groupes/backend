package com.HelloWay.HelloWay.controllers;

import com.HelloWay.HelloWay.entities.Space;
import com.HelloWay.HelloWay.entities.Zone;
import com.HelloWay.HelloWay.payload.response.MessageResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.HelloWay.HelloWay.entities.Categorie;
import com.HelloWay.HelloWay.services.CategorieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategorieController {

    CategorieService categorieService;

    @Autowired
    public CategorieController(CategorieService categorieService) {
        this.categorieService = categorieService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    @ResponseBody
    public Optional<Categorie> addNewCategorie(@RequestBody Categorie categorie) throws Exception {
        return categorieService.addCategorie(categorie);
    }

    @JsonIgnore
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    @ResponseBody
    public List<Categorie> allCategories(){
        return categorieService.findAllCategories();
    }


    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    @ResponseBody
    public Categorie findCategorieById(@PathVariable("id") long id){
        return categorieService.findCategorieById(id);
    }


    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    @ResponseBody
    public Categorie updateCategorie(@RequestBody Categorie categorie){
        return categorieService.updateCategorie(categorie); }

    @PutMapping("/update/{categorieId}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    @ResponseBody
    public ResponseEntity<?> updateCategorie(@RequestBody Categorie categorie, @PathVariable long categorieId){
        Categorie exestingCategorie = categorieService.findCategorieById(categorieId);
        Space space = exestingCategorie.getSpace();
        List<Categorie> spaceCategories = space.getCategories();
        spaceCategories.remove(exestingCategorie);
        for (Categorie c : spaceCategories){
            if (c.getCategoryTitle().equals(categorie.getCategoryTitle())){
                return ResponseEntity.badRequest().body("categorie exist with this title please try with an other");
            }
        }
        return ResponseEntity.ok().body(categorieService.updateCategorie(categorie));
    }

    //TODO ::
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    @ResponseBody
    public void deleteCategorie(@PathVariable("id") long id){
        //delete all products attached with this categorie
        categorieService.deleteCategorie(id); }

    @PostMapping("/add/id_space/{id_space}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    @ResponseBody
    public ResponseEntity<?> addNewCategorieByIdSpace(@RequestBody Categorie categorie, @PathVariable Long id_space)  {
        if (categorieService.categorieExistsByTitleInSpace(categorie, id_space)){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Categorie title is already taken!"));
        }else
        {
        Categorie categorieObject =  categorieService.addCategorieByIdSpace(categorie, id_space);
        return ResponseEntity.ok().body(categorieObject);
        }
    }

    @GetMapping("/id_space/{id_space}")
    @PreAuthorize("hasAnyRole('PROVIDER','USER', 'GUEST','WAITER')")
    @ResponseBody
    public List<Categorie> getCategoriesByIdSpace( @PathVariable Long id_space) {
        return categorieService.getCategoriesByIdSpace( id_space);
    }



    @GetMapping("/all/paging")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public Page<Categorie> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return categorieService.getCategories(page, size);
    }



}
