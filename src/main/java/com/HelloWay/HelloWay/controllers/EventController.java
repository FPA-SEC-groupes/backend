package com.HelloWay.HelloWay.controllers;

import com.HelloWay.HelloWay.entities.*;
import com.HelloWay.HelloWay.repos.ImageRepository;
import com.HelloWay.HelloWay.services.EventService;
import com.HelloWay.HelloWay.services.NotificationService;
import com.HelloWay.HelloWay.services.ProductService;
import com.HelloWay.HelloWay.services.SpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private SpaceService spaceService;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    ProductService productService;

    @Autowired
    NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<Event> getAllEvents() {
        return eventService.findAllEvents();
    }

    @GetMapping("/{eventId}")
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    public ResponseEntity<Event> getEventById(@PathVariable Long eventId) {
        Event event = eventService.findEventById(eventId);
        return ResponseEntity.ok(event);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    public Event createEvent(@RequestBody Event event) {
        return eventService.createEvent(event);
    }

    @PostMapping("/space/{spaceId}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<?> createEventForSpace(@RequestBody Event event, @PathVariable long spaceId) {
        Space space = spaceService.findSpaceById(spaceId);
        if (space == null){
            return ResponseEntity.badRequest().body("space does not exist with this id " + spaceId);
        }
        event.setSpace(space);
        Event eventObject = eventService.createEvent(event);
        List<Event> events = new ArrayList<>();
        events = space.getEvents();
        events.add(eventObject);
        space.setEvents(events);
        spaceService.updateSpace(space);
        return ResponseEntity.ok().body(eventObject);
    }

    @PostMapping("/promotion/space/{spaceId}/{productId}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<?> createPromotionForSpaceAndProduct(@RequestBody Promotion promotion,
                                                     @PathVariable long spaceId,
                                                     @PathVariable long productId) {
        Space space = spaceService.findSpaceById(spaceId);
        if (space == null){
            return ResponseEntity.badRequest().body("space does not exist with this id " + spaceId);
        }
        Product product = productService.findProductById(productId);
        if (product == null){
            return ResponseEntity.badRequest().body("product does not exist with this id " + productId);
        }
        promotion.setSpace(space);
        promotion.setProduct(product);
        Promotion promotionObject = eventService.createPromotion(promotion);
        List<Event> events = new ArrayList<>();
        events = space.getEvents();
        events.add(promotionObject);
        space.setEvents(events);
        spaceService.updateSpace(space);
        List<Promotion> productEvents = new ArrayList<>();
        productEvents = product.getPromotions();
        productEvents.add(promotionObject);
        productService.updateProduct(product);

        List<User> spaceUsers = new ArrayList<>();
        spaceUsers = space.getUsers();
        for (User user : spaceUsers) {
            String message = String.format("Dear %s,\n\n", user.getName()) +
                    String.format("We have an exciting promotion at %s!\n\n", space.getTitleSpace()) +
                    "Promotion Details:\n" +
                    String.format("- Product: %s\n", product.getProductTitle()) +
                    String.format("- Discount: %.2f%% off\n\n", promotion.getPercentage()) +
                    String.format("- Date: %s\n", promotion.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) +
                    String.format("- Time: %s - %s\n\n", promotion.getStartDate().format(DateTimeFormatter.ofPattern("HH:mm")), promotion.getEndDate().format(DateTimeFormatter.ofPattern("HH:mm"))) +
                    "We hope to see you there!\n\n" +
                    "Don't miss out on this amazing deal!\n\n" +
                    "Best regards,\n" +
                    space.getTitleSpace() + " Team";
            notificationService.createNotification("New Promotion Announcement",message, user);

        }

        return ResponseEntity.ok().body(promotionObject);
    }

    @PostMapping("/promotion")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public Promotion createPromotion(@RequestBody Promotion promotion) {
        return eventService.createPromotion(promotion);
    }

    @PostMapping("/party/space/{spaceId}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<Party> createParty(@PathVariable Long spaceId, @RequestBody Party party) {
        // Retrieve the space
        Space space = spaceService.findSpaceById(spaceId);

        // Check if the space exists
        if (space == null) {
            return ResponseEntity.notFound().build();
        }

        // Initialize the events list if it's null
        if (space.getEvents() == null) {
            space.setEvents(new ArrayList<>());
        }

        // Set the space for the party
        party.setSpace(space);

        // Save the party
        Party createdParty = eventService.createParty(party);

        // Add the party to the events list
        space.getEvents().add(party);
        spaceService.updateSpace(space);

        List<User> spaceUsers = new ArrayList<>();
        spaceUsers = space.getUsers();
        for (User user : spaceUsers) {
            String message =  String.format("Dear %s,\n\n", user.getName()) +
                    String.format("You are invited to a party at %s!\n\n", space.getTitleSpace()) +
                    "Party Details:\n" +
                    String.format("- Event: %s\n", party.getEventTitle()) +
                    String.format("- Price: %s\n", party.getPrice()) +
                    String.format("- Participant Number: %s\n", party.getNbParticipant()) +
                    String.format("- Date: %s\n", party.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) +
                    String.format("- Time: %s - %s\n\n", party.getStartDate().format(DateTimeFormatter.ofPattern("HH:mm")), party.getEndDate().format(DateTimeFormatter.ofPattern("HH:mm"))) +
                    "We hope to see you there!\n\n" +
                    "Best regards,\n" +
                    space.getTitleSpace() + " Team";
            notificationService.createNotification("Party Invitation",message, user);

        }

        party.setNbParticipant(party.getNbParticipant() - 1);
        return ResponseEntity.ok(createdParty);
    }



    @PostMapping("/party")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public Party createParty(@RequestBody Party party) {
        return eventService.createParty(party);
    }

    @GetMapping("/promotions")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public List<Promotion> getAllPromotions() {
        return eventService.getAllPromotions();
    }

    @GetMapping("/parties")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public List<Party> getAllParties() {
        return eventService.getAllParties();
    }

    @PutMapping("/{eventId}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public Event updateEvent(@PathVariable Long eventId, @RequestBody Event updatedEvent) {
        return eventService.updateEvent(eventId, updatedEvent);
    }

    @GetMapping("/spaces/{spaceId}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public List<Event> getEventsBySpaceId(@PathVariable Long spaceId) {
        return eventService.getEventsBySpaceId(spaceId);
    }

    @GetMapping("/promotions/product/{productId}")
    @PreAuthorize("hasAnyRole('PROVIDER', 'USER', 'GUEST')")
    public ResponseEntity<?> getPromotionsByProductId(@PathVariable Long productId) {
        Product product = productService.findProductById(productId);
        if (product == null){
            return ResponseEntity.notFound().build();
        }
        List<Promotion> promotions = product.getPromotions();
        return ResponseEntity.ok().body(promotions);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('PROVIDER', 'USER', 'GUEST')")
    public List<Event> getEventsByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return eventService.getEventsByDateRange(startDate, endDate);
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasAnyRole('PROVIDER', 'USER', 'GUEST')")
    public List<Event> getUpcomingEvents(@RequestParam("limit") int limit) {
        return eventService.getUpcomingEvents(limit);
    }

    @PostMapping("/{id}/images")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<String> addImage(@PathVariable("id") Long id,
                                           @RequestParam("file") MultipartFile file) {
        try {
            Event event = eventService.findEventById(id);

            // Create the Image entity and set the reference to the event entity
            Image image = new Image();
            image.setEvent(event);
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setData(file.getBytes());

            // Persist the Image entity to the database
            imageRepository.save(image);

            return ResponseEntity.ok().body("Image uploaded successfully");
        } catch (IOException ex) {
            throw new RuntimeException("Error uploading file", ex);
        }
    }

    @PutMapping("/update/promotion")
    @ResponseBody
    public Event updatePromotion(@RequestBody Promotion promotion){
        Promotion existedPromotion = (Promotion) eventService.findEventById(promotion.getIdEvent());
        promotion.setProduct(existedPromotion.getProduct());
        promotion.setSpace(existedPromotion.getSpace());
        return eventService.updateEvent(promotion); }

    @PutMapping("/update")
    @ResponseBody
    public Event updateEvent(@RequestBody Event event){
        Event existedEvent = eventService.findEventById(event.getIdEvent());
        event.setSpace(existedEvent.getSpace());
        return eventService.updateEvent(event); }

    @DeleteMapping("{idImage}/images/{idEvent}")
    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER')")
    public ResponseEntity<?> deleteImage(@PathVariable String idImage, @PathVariable Long idEvent){
        Image image = imageRepository.findById(idImage).orElse(null);
        if (image == null){
            return ResponseEntity.notFound().build();
        }
        Event event = eventService.findEventById(idEvent);
        if (event == null){
            return ResponseEntity.notFound().build();
        }
        event.getImages().remove(image);
        eventService.updateEvent(event);
        imageRepository.delete(image);
        return ResponseEntity.ok("image deleted successfully for the event");
    }

}
