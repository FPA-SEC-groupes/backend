package com.HelloWay.HelloWay.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.HelloWay.HelloWay.entities.Restrictions;
import com.HelloWay.HelloWay.entities.EmailDetails;
import com.HelloWay.HelloWay.entities.Reservation;
import com.HelloWay.HelloWay.entities.User;
import com.HelloWay.HelloWay.exception.RestrictionNotFoundException;
import com.HelloWay.HelloWay.payload.request.RestrictionsDTO;
import com.HelloWay.HelloWay.repos.RestrictionsRepository;
import com.HelloWay.HelloWay.repos.ReservationRepository;
import com.HelloWay.HelloWay.repos.UserRepository;

@Service
public class RestrictionsService {

    @Autowired
    private RestrictionsRepository restrictionsRepository;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ReservationRepository reservationRepo;

    @Autowired
    EmailService emailService;

    public List<Restrictions> getAllRestrictions() {
        return restrictionsRepository.findAll();
    }

    public Optional<Restrictions> getRestrictionsById(Long id) {
        return restrictionsRepository.findById(id);
    }

    public Restrictions createRestrictions(RestrictionsDTO restrictionsDTO) {
        // Find the user
        User user = userRepo.findById(restrictionsDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    
        // Find the reservation
        Reservation reservation = reservationRepo.findById(restrictionsDTO.getReservationId())
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
    
        // Create the restriction
        Restrictions restriction = new Restrictions();
        restriction.setDescription(restrictionsDTO.getDescription());
        restriction.setUser(user);
        restriction.setReservation(reservation);
    
        // Increment the user's number of restrictions
        Integer numberOfRestrictions = user.getNumberOfRestrictions() != 0 ? user.getNumberOfRestrictions() : 0;
        user.setNumberOfRestrictions(numberOfRestrictions + 1);
        userRepo.save(user);
    
        // Save the restriction
        Restrictions savedRestriction = restrictionsRepository.save(restriction);
        String subject = "Notification de nouvelle restriction sur votre compte Snapwaiter";

        String message = "Bonjour,\n\n"
                        + "Nous vous informons qu'une nouvelle restriction a été appliquée à votre compte Snapwaiter. Vous trouverez les détails ci-dessous :\n\n"
                        + "Description : " + restrictionsDTO.getDescription() + "\n"
                        + "ID de réservation : " + restrictionsDTO.getReservationId() + "\n\n"
                        + "Nombre actuel de restrictions sur votre compte : " + user.getNumberOfRestrictions() + "\n\n"
                        + "Si vous n'êtes pas à l'origine de cette restriction ou si vous souhaitez contester cette décision, veuillez nous contacter immédiatement à l'adresse suivante : contact.Helloway@gmail.com.\n\n"
                        + "Merci de votre compréhension.\n\n"
                        + "Cordialement,\n"
                        + "L'équipe HelloWay";
        // String subject = "Obtenir une nouvelle restriction";
        // String message = "Bonjour de HelloWay,\n\n"
        //         + "Vous avez reçu une nouvelle restriction. Détails:\n"
        //         + "Description: " + restrictionsDTO.getDescription() + "\n"
        //         + "Reservation ID: " + restrictionsDTO.getReservationId() + "\n\n"
        //         + "Votre nombre actuel de restrictions : " + user.getNumberOfRestrictions() + "\n\n"
        //         + "If you did not request this restriction, please contact us immediately.\n\n"
        //         + "Cordialement,\n"
        //         + "L'équipe HelloWay";
        EmailDetails details = new EmailDetails(user.getEmail(), message, subject);
        emailService.sendSimpleMail(details);    
        return savedRestriction;
    }
        
    public ResponseEntity<?> findByReservationId(Long reservationId) {
    Restrictions restriction = restrictionsRepository.findByReservationId(reservationId);

    // If restriction exists, return it; otherwise, return a custom message.
    if (restriction != null) {
        return new ResponseEntity<>(restriction, HttpStatus.OK); // Return the restriction with 200 OK
    } else {
        // Return a default message or object if no restriction is found
        return new ResponseEntity<>("No restriction found for reservation ID: " + reservationId, HttpStatus.OK);
    }
}

    
    
    

    public Restrictions updateRestrictions(Long id, RestrictionsDTO restrictionsDTO) {
        Optional<Restrictions> existingRestrictions = restrictionsRepository.findById(id);
        if (existingRestrictions.isPresent()) {
            Restrictions restrictions = existingRestrictions.get();
            restrictions.setDescription(restrictionsDTO.getDescription());
            restrictions.setUser(userRepo.findById(restrictionsDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found")));
            return restrictionsRepository.save(restrictions);
        } else {
            throw new RuntimeException("Restrictions not found");
        }
    }

    public void deleteRestrictions(Long id) {
        Optional<Restrictions> restriction = restrictionsRepository.findById(id);
        if (restriction.isPresent()) {
            User user = restriction.get().getUser();
            user.setNumberOfRestrictions(user.getNumberOfRestrictions() - 1);
            userRepo.save(user);
            restrictionsRepository.deleteById(id);
        } else {
            throw new RuntimeException("Restrictions not found");
        }
    }

    public int getNumberOfRestrictionsByUserId(Long userId) {
        return restrictionsRepository.countByUserId(userId);
    }
}
