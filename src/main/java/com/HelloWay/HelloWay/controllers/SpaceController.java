package com.HelloWay.HelloWay.controllers;

import com.HelloWay.HelloWay.entities.*;
import com.HelloWay.HelloWay.payload.request.SignupRequest;
import com.HelloWay.HelloWay.payload.response.MessageResponse;
import com.HelloWay.HelloWay.payload.response.SpaceDTO;
import com.HelloWay.HelloWay.repos.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.HelloWay.HelloWay.services.ImageService;
import com.HelloWay.HelloWay.services.SpaceService;
import com.google.zxing.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.HelloWay.HelloWay.entities.ERole.ROLE_WAITER;

@RestController
@RequestMapping("/api/spaces")
public class SpaceController {

    SpaceService spaceService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    ImageService imageService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    SpaceRepository spaceRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    ProductRepository productRepository ;
    @Autowired
    public SpaceController(SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ResponseBody
    public Space addNewSpace(@RequestBody Space space) throws IOException {
        return spaceService.addNewSpace(space);
    }

    @JsonIgnore
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ResponseBody
    public List<Space> allSpaces(){

        return spaceService.findAllSpaces();
    }

    @GetMapping("/all/dto")
    @ResponseBody
    public ResponseEntity<?> allSpacesDto() {
        List<Space> spaces = spaceService.findAllSpaces();
        List<SpaceDTO> spaceDtos = new ArrayList<>();

        for (Space space : spaces) {

            // Map other properties as needed

            spaceDtos.add(modelMapper.map(space, SpaceDTO.class));
        }

        return ResponseEntity.ok().body(spaceDtos);
    }

    public SpaceDTO convertToDto(Space entity) {
        return modelMapper.map(entity, SpaceDTO.class);
    }


    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('WAITER', 'USER', 'GUEST','PROVIDER')")
    @ResponseBody
    public Space findSpaceById(@PathVariable("id") long id){
        return spaceService.findSpaceById(id);
    }


    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER')")
    @ResponseBody
    public Space updateSpace(@RequestBody Space space){
        return spaceService.updateSpace(space); }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER')")
    @ResponseBody
    public void deleteSpace(@PathVariable("id") long id){
        spaceService.deleteSpace(id); }
    //Pas encore tester : Que apres les crud de categorie Controller
    @PostMapping("/add/idModerator/{idModerator}/idCategory/{idCategory}")
    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER')")
    @ResponseBody
    public Space addNewSpaceByIdModeratorAndIdCategory(@RequestBody Space space, @PathVariable Long idModerator, @PathVariable Long idCategory) {
        return spaceService.addSpaceByIdModeratorAndIdSpaceCategory(space, idModerator, idCategory);
    }

    @PostMapping("/add/idModerator/{idModerator}/idSpaceCategory/{idSpaceCategory}")
    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER')")
    @ResponseBody
    public Space addNewSpaceByIdModeratorAndIdSpaceCategory(@RequestBody Space space, @PathVariable Long idModerator, @PathVariable Long idSpaceCategory) {
        return spaceService.addSpaceByIdModeratorAndSpaceCategory(space, idModerator, idSpaceCategory);
    }

    @GetMapping("/idModerator/{idModerator}")
    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER')")
    @ResponseBody
    public Space getSpaceByIdModerator( @PathVariable Long idModerator) {
        return spaceService.getSpaceByIdModerator(idModerator);
    }

    @GetMapping("/idCategory/{idCategory}")
    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER', 'WAITER', 'USER', 'GUEST')")
    @ResponseBody
    public Space getSpaceByIdCategory( @PathVariable Long idCategory) {
        // get spaceByIdSpaceCategorie
        return spaceService.getSpaceByIdCategory(idCategory);
    }


    // Done without testing
    @GetMapping("/idSpaceCategory/{idSpaceCategory}")
    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER')")
    @ResponseBody
    public List<Space> getSpacesByIdSpaceCategory( @PathVariable Long idSpaceCategory) {

        return spaceService.getSpacesByIdSpaceCategory(idSpaceCategory);
    }

    @PostMapping("/images")
    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER')")
    public ResponseEntity<?> addSpaceWithImages(@RequestParam("titleSpace") String titleSpace,
                                                @RequestParam("latitude") String latitude,
                                                @RequestParam("longitude") String longitude,
                                                @RequestParam("rating") Float rating,
                                                @RequestParam("numberOfRating") int numberOfRating,
                                                @RequestParam("description") String description,
                                                @RequestParam("images") List<MultipartFile> images) throws IOException {
        Space space = new Space();
        space.setTitleSpace(titleSpace);
        space.setLatitude(latitude);
        space.setLongitude(longitude);
        space.setRating(rating);
        space.setNumberOfRating(numberOfRating);
        space.setDescription(description);

        List<Image> spaceImages = new ArrayList<>();
        for (MultipartFile image : images) {
            Image spaceImage = new Image();
            spaceImage.setFileName(image.getOriginalFilename());
            spaceImage.setFileType(image.getContentType());
            spaceImage.setData(image.getBytes());
            spaceImage.setSpace(space);
            imageService.addImageLa(spaceImage);
            spaceImages.add(spaceImage);
        }
        space.setImages(spaceImages);


        spaceService.addNewSpace(space);

        return ResponseEntity.ok("Space created successfully with images.");
    }

    @PostMapping("/{id}/images")
    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER')")
    public ResponseEntity<String> addImage(@PathVariable("id") Long id,
                                           @RequestParam("file") MultipartFile file) {
        try {
            Space space = spaceRepository.findById(id).orElseThrow(() -> new ChangeSetPersister.NotFoundException()) ;

            // Create the Image entity and set the reference to the Space entity
            Image image = new Image();
            image.setSpace(space);
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setData(file.getBytes());

            // Persist the Image entity to the database
            imageRepository.save(image);

            return ResponseEntity.ok().body("Image uploaded successfully");
        } catch (IOException ex) {
            throw new RuntimeException("Error uploading file", ex);
        } catch (ChangeSetPersister.NotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    @DeleteMapping("{idImage}/images/{idSpace}")
    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER')")
    public ResponseEntity<?> deleteImage(@PathVariable String idImage, @PathVariable Long idSpace){
        Image image = imageService.getImage(idImage);
        if (image == null){
            return ResponseEntity.notFound().build();
        }
        Space space = spaceService.findSpaceById(idSpace);
        if (space == null){
            return ResponseEntity.notFound().build();
        }
        space.getImages().remove(image);
        spaceService.updateSpace(space);
        imageRepository.delete(image);
        return ResponseEntity.ok("image deleted successfully for the space");
    }


    @PostMapping("/{idSpace}/images/{idProduct}")
    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER','WAITER')")
    public ResponseEntity<String> addImageBySpaceIdAndProductId(@PathVariable("idSpace") Long id,
                                                                @RequestParam("file") MultipartFile file, @PathVariable Long idProduct) {
        try {
            Space space = spaceRepository.findById(id).orElseThrow(() -> new ChangeSetPersister.NotFoundException()) ;
            Product product = productRepository.findById(idProduct).orElse(null);
            // Create the Image entity and set the reference to the Space entity
            Image image = new Image();
            image.setSpace(space);
            image.setProduct(product);
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setData(file.getBytes());

            // Persist the Image entity to the database
            imageRepository.save(image);

            return ResponseEntity.ok().body("Image uploaded successfully");
        } catch (IOException ex) {
            throw new RuntimeException("Error uploading file", ex);
        } catch (ChangeSetPersister.NotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/moderatorUserId/{moderatorUserId}/{spaceId}/servers/{serverId}/zones/{zoneId}")
    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER')")
    public ResponseEntity<String> setServerInZone(
            @PathVariable Long spaceId,
            @PathVariable Long moderatorUserId,
            @PathVariable Long serverId,
            @PathVariable Long zoneId) {
        try {
            spaceService.setServerInZone(spaceId, moderatorUserId, serverId, zoneId);
            return ResponseEntity.ok("Server successfully assigned to the zone.");
        } catch (NotFoundException e) {
            // Handle the exception
            // For example, return an appropriate error response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found.");
        }

    }

    @PostMapping("/moderatorUserId/{moderatorUserId}/{spaceId}/servers/{serverId}")
    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER')")
    public ResponseEntity<String> addServerInSpace(
            @PathVariable Long spaceId,
            @PathVariable Long moderatorUserId,
            @PathVariable Long serverId) {
        try {
            spaceService.addServerInSpace(spaceId, moderatorUserId, serverId);
            return ResponseEntity.ok("Server successfully assigned to the Space.");
        } catch (NotFoundException e) {
            // Handle the exception
            // For example, return an appropriate error response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found.");
        }

    }

    @PostMapping("/moderatorUserId/{moderatorUserId}/{spaceId}/servers")
    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER')")
    @ResponseBody
    public ResponseEntity<?> createServerForSpace(
            @PathVariable Long spaceId,
            @PathVariable Long moderatorUserId,
            @RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account ya 3loulou
        User user = new User(signupRequest.getUsername(),
                signupRequest.getName(),
                signupRequest.getLastname(),
                signupRequest.getBirthday(),
                signupRequest.getPhone(),
                signupRequest.getEmail(),
                encoder.encode(signupRequest.getPassword()));

        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();
        Role assistantRole = roleRepository.findByName(ROLE_WAITER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(assistantRole);
        user.setRoles(roles);
        user.setActivated(true);
        User userSaved = userRepository.save(user);
        try {
            spaceService.addServerInSpace(spaceId, moderatorUserId, userSaved.getId());
            return ResponseEntity.ok("Server successfully assigned to the Space.");
        } catch (NotFoundException e) {
            // Handle the exception
            // For example, return an appropriate error response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found.");
        }

    }

    @GetMapping("/all/paging")
    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER', 'WAITER', 'USER', 'GUEST')")
    public Page<Space> getSpaces(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return spaceService.getSpaces(page, size);
    }

    @GetMapping("/servers/{spaceId}")
    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER')")

    @ResponseBody
    public ResponseEntity<?> getServersByIdSpace(@PathVariable long spaceId){
        Space space = spaceService.findSpaceById(spaceId);
        if (space == null){
            return ResponseEntity.badRequest().body("space doesn't exist");
        }
        return ResponseEntity.ok().body(spaceService.getServersBySpace(space));
    }

    @GetMapping("/server/{serverId}")
    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER','WAITER')")
    @ResponseBody
    public ResponseEntity<?> getSpaceByIdServer(@PathVariable long serverId){
        User waiter = userRepository.findById(serverId).orElse(null);
        if (waiter == null){
            return ResponseEntity.badRequest().body("waiter doesn't exist with this id : " + serverId);
        }
        return ResponseEntity.ok().body(spaceService.getSpaceByWaiterId(waiter));
    }

    @PostMapping("/add/rate/{spaceId}/{rate}")
    @PreAuthorize("hasAnyRole('USER')")
    @ResponseBody
    public ResponseEntity<?> addRateToSpace(@PathVariable long spaceId, @PathVariable long rate)  {

        Space space = spaceService.findSpaceById(spaceId);
        if (space == null){
            return ResponseEntity.notFound().build();
        }
        space = spaceService.addNewRate(space, (float)rate);
        return  ResponseEntity.ok().body(space);
    }

    @GetMapping("/nearest")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> getTheNearestSpacesByDistance(@RequestParam("userLatitude") String userLatitude,
                                                     @RequestParam("userLongitude") String userLongitude,
                                                     @RequestParam("threshold") double threshold){
        List<Space> spaces = spaceService.getTheNearestSpacesByDistance(userLatitude, userLongitude,
                threshold);
        List<SpaceDTO> spaceDtos = new ArrayList<>();

        for (Space space : spaces) {

            // Map other properties as needed

            spaceDtos.add(modelMapper.map(space, SpaceDTO.class));
        }

        return ResponseEntity.ok().body(spaceDtos);

    }


}
