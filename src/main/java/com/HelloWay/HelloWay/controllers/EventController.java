package com.HelloWay.HelloWay.controllers;

import com.HelloWay.HelloWay.config.FileUploadUtil;
import com.HelloWay.HelloWay.entities.*;
import com.HelloWay.HelloWay.payload.request.EventDTO;
import com.HelloWay.HelloWay.repos.EventRepository;
import com.HelloWay.HelloWay.repos.ImageRepository;
import com.HelloWay.HelloWay.services.EventService;
import com.HelloWay.HelloWay.services.NotificationService;
import com.HelloWay.HelloWay.services.ProductService;
import com.HelloWay.HelloWay.services.SpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    EventRepository eventRepository ;

    @Autowired
    private SpaceService spaceService;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    ProductService productService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    private MessageSource messageSource;

    private boolean isSameDay(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1.toLocalDate().equals(dateTime2.toLocalDate());
    }
    
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
            Locale userLocale = new Locale(user.getPreferredLanguage());
            // String PromotionTitle = messageSource.getMessage("promotionTitle", null, userLocale);
            // String template = messageSource.getMessage("promotion.message", null, userLocale);
            List<String>parames= new ArrayList<>();
            List<String> paramesClient = new ArrayList<>();
            parames.add(0,String.valueOf(user.getName()));
            parames.add(1,String.valueOf(space.getTitleSpace()));
            parames.add(2,String.valueOf(product.getProductTitle()));
            parames.add(3,String.valueOf(promotion.getPercentage()));
            parames.add(4,String.valueOf(promotion.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
            parames.add(5,String.valueOf(promotion.getStartDate().format(DateTimeFormatter.ofPattern("HH:mm"))));
            parames.add(6,String.valueOf(promotion.getEndDate().format(DateTimeFormatter.ofPattern("HH:mm"))));
            // String formattedMessage = MessageFormat.format(template,
            //         user.getName(),
            //         space.getTitleSpace(),
            //         product.getProductTitle(),
            //         promotion.getPercentage(),
            //         promotion.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            //         promotion.getStartDate().format(DateTimeFormatter.ofPattern("HH:mm")),
            //         promotion.getEndDate().format(DateTimeFormatter.ofPattern("HH:mm"))
            // );
            notificationService.createNotification("promotionTitle","promotion.message",parames, user);

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
    public ResponseEntity<?> createParty(@PathVariable Long spaceId, @RequestBody Party party) {
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

        List<Party> existingParties = eventRepository.findByEventTitle(party.getEventTitle());

        for (Party party3 : existingParties) {
            Space space2 = party3.getSpace();
            if (space2.getId_space().equals(spaceId) && isSameDay(party3.getStartDate(), party.getStartDate())) {
                return ResponseEntity.ok("event existe");
            }
        }

        // Save the party
        Party createdParty = eventService.createParty(party);

        // Add the party to the events list
        space.getEvents().add(party);
        spaceService.updateSpace(space);

        // Notify users
        List<User> spaceUsers = space.getUsers();
        for (User user : spaceUsers) {
            Locale userLocale = new Locale(user.getPreferredLanguage());

            String partyTitle = messageSource.getMessage("partyTitle", null, userLocale);
            String template = messageSource.getMessage("party.invitation", null, userLocale);

            List<String> parames = new ArrayList<>();
            parames.add(String.valueOf(user.getName()));
            parames.add(String.valueOf(space.getTitleSpace()));
            parames.add(String.valueOf(party.getEventTitle()));
            parames.add(String.valueOf(party.getPrice()));
            parames.add(String.valueOf(party.getNbParticipant()));
            parames.add(String.valueOf(party.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
            parames.add(String.valueOf(party.getStartDate().format(DateTimeFormatter.ofPattern("HH:mm"))));
            parames.add(String.valueOf(party.getEndDate().format(DateTimeFormatter.ofPattern("HH:mm"))));

            notificationService.createNotification("partyTitle", "party.invitation", parames, user);
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
    public ResponseEntity<String> addImage(@PathVariable("id") Long id, @RequestParam("file") MultipartFile file) {
        try {
            Event event = eventService.findEventById(id);
            if (event == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
            }

            // Create the Image entity and set the reference to the event entity
            Image image = new Image();
            LocalDateTime currentDateTime = LocalDateTime.now();
            String orgFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String ext = orgFileName.substring(orgFileName.lastIndexOf("."));
            String uploadDir = "photos/event/";
            String fileName = orgFileName.substring(0, orgFileName.lastIndexOf(".")) + "_" + currentDateTime.toString().replaceAll(":", "-") + ext;
            FileUploadUtil.saveFile(uploadDir, fileName, file);

            image.setEvent(event);
            image.setFileName(fileName);
            image.setFileType(file.getContentType());

            // Persist the Image entity to the database
            imageRepository.save(image);

            return ResponseEntity.ok().body("Image uploaded successfully");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
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
        public ResponseEntity<?> updateEvent(@RequestBody EventDTO event) {
            try {
                Event existedEvent = eventService.findEventById(event.getIdEvent());
                if (existedEvent == null) {
                    return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);
                }
                // Ensure the space exists
                Space existingSpace = existedEvent.getSpace();
                if (existingSpace != null) {
                    existedEvent.setSpace(spaceService.findSpaceById(existingSpace.getId_space()));
                }
        
                Event updatedEvent = eventService.updateDetailsEvent(event);
                return new ResponseEntity<>(updatedEvent, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
            

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
    @DeleteMapping("/{idEvent}")
    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER')")
    public ResponseEntity<?> deleteEvent( @PathVariable Long idEvent){
        
        Event event = eventService.findEventById(idEvent);
        if (event == null){
            return ResponseEntity.notFound().build();
        }
        // Image image = imageRepository.findById(idImage).orElse(null);
        // if (image == null){
        //     return ResponseEntity.notFound().build();
        // }
        // event.getImages().remove(image);
        eventService.deleteEvent(idEvent);
        // imageRepository.delete(image);
        return ResponseEntity.ok("Event deleted successfully");
    }
}
