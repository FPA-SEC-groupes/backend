package com.HelloWay.HelloWay.controllers;

import com.HelloWay.HelloWay.entities.Board;
import com.HelloWay.HelloWay.entities.EReservation;
import com.HelloWay.HelloWay.entities.Reservation;
import com.HelloWay.HelloWay.services.NotificationService;
import com.HelloWay.HelloWay.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;
    private final NotificationService notificationService;

    @Autowired
    public ReservationController(ReservationService reservationService,
                                 NotificationService notificationService) {
        this.reservationService = reservationService;
        this.notificationService = notificationService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.findAllReservations();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROVIDER','USER')")
    public ResponseEntity<?> getReservationById(@PathVariable Long id) {
        Reservation reservation = reservationService.findReservationById(id);
        if (reservation == null) {
            // Return a proper response with a JSON message when the reservation does not exist
            String errorMessage = "{\"message\": \"Reservation not found\"}";
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorMessage);
        }
        return ResponseEntity.ok(reservation);
    }

    @GetMapping("/tables/{id}")
    @PreAuthorize("hasAnyRole('PROVIDER','USER')")
    public ResponseEntity<?> getTablesByIdReservation(@PathVariable Long id) {
        Reservation reservation = reservationService.findReservationById(id);
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(reservation.getBoards());
    }

    @GetMapping("/availability/{spaceId}")
    @PreAuthorize("hasAnyRole('PROVIDER','USER')")
    public ResponseEntity<?> getTablesByDisponibilitiesDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @PathVariable long spaceId) {
        List<Board> availableTables = reservationService.getTablesByDisponibilitiesDate(date, spaceId);
        if (availableTables.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(availableTables);
    }

    @PostMapping("/space/{spaceId}/user/{userId}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<Reservation> createReservation(
            @RequestBody Reservation reservation,
            @PathVariable Long spaceId,
            @PathVariable Long userId
    ) {
        Reservation createdReservation = reservationService.createReservationForSpaceAndUser(reservation, spaceId, userId);
        return ResponseEntity.ok(createdReservation);
    }

    @PostMapping("/space/{spaceId}/user/{userId}/board/boardId")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<Reservation> createReservationWithBoard(
            @RequestBody Reservation reservation,
            @PathVariable Long spaceId,
            @PathVariable Long userId,
            @PathVariable Long boardId
    ) {
        Reservation createdReservation = reservationService.createReservationForSpaceAndUserWithBoard(reservation, spaceId, userId, boardId);
        return ResponseEntity.ok(createdReservation);
    }

    @PostMapping("/assign/reservation/{reservationId}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<Reservation> assignReservationToTables(
            @RequestBody List<Long> boardIds,
            @PathVariable long reservationId) {
        Reservation reservation = reservationService.findReservationById(reservationId);
        if (reservation == null){
            return ResponseEntity.notFound().build();
        }
        Reservation createdReservation = reservationService.assignReservationToTables(boardIds, reservation);
        return ResponseEntity.ok(createdReservation);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROVIDER','USER')")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable Long id,
            @RequestBody Reservation reservation
    ) {
        Reservation existingReservation = reservationService.findReservationById(id);
        if (existingReservation == null) {
            return ResponseEntity.notFound().build();
        }
        reservation.setIdReservation(id);
        Reservation updatedReservation = reservationService.updateReservation(reservation);
        return ResponseEntity.ok(updatedReservation);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<?> deleteReservation(@PathVariable Long id) {
        Reservation reservation = reservationService.findReservationById(id);
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }
        reservationService.deleteReservation(id);
        return ResponseEntity.ok("Reservation deleted successfully");
    }

    @GetMapping("/space/{spaceId}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<List<Reservation>> getReservationsBySpaceId(@PathVariable Long spaceId) {
        List<Reservation> reservations = reservationService.findReservationsBySpaceId(spaceId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('PROVIDER','USER')")
    public ResponseEntity<List<Reservation>> getReservationsByUserId(@PathVariable Long userId) {
        List<Reservation> reservations = reservationService.findReservationsByUserId(userId);
        return ResponseEntity.ok(reservations);
    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<Reservation> acceptReservation(@PathVariable Long id) {
        Reservation reservation = reservationService.findReservationById(id);
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }

        // Update the reservation status to "ACCEPTED" (assuming you have an appropriate enumeration for status)
        reservation.setStatus(EReservation.CONFIRMED);

        reservation.setConfirmedDate(LocalDateTime.now());

        Reservation updatedReservation = reservationService.updateReservation(reservation);

        // Create in-app notification for users

        String messageForTheUser = "congratulation " + reservation.getUser().getName()+ " your reservation have been Confirmed  , To  :   " + reservation.getStartDate()  + " , for your : " + reservation.getEventTitle() + ", with board of number : " + reservation.getBoards().get(0).getNumTable();
        notificationService.createNotification("Reservation Notification",messageForTheUser, reservation.getUser());

        return ResponseEntity.ok(updatedReservation);
    }


    @PostMapping("/{id}/refuse")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<Reservation> refuseReservation(@PathVariable Long id) {
        Reservation reservation = reservationService.findReservationById(id);
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }

        // Update the reservation status to "REFUSED" (assuming you have an appropriate enumeration for status)
        reservation.setStatus(EReservation.REFUSED);

        Reservation updatedReservation = reservationService.updateReservation(reservation);

        // Create in-app notification for users

        String messageForTheUser = "Sorry " + reservation.getUser().getName()+ " your reservation have been Refused  , To  :   " + reservation.getStartDate()  + " , for your : " + reservation.getEventTitle();
        notificationService.createNotification("Reservation Notification",messageForTheUser, reservation.getUser());

        return ResponseEntity.ok(updatedReservation);
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('PROVIDER','USER')")
    public ResponseEntity<Reservation> cancelReservation(@PathVariable Long id) {
        Reservation reservation = reservationService.findReservationById(id);
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }

        // Update the reservation status to "CANCELED" (assuming you have an appropriate enumeration for status)
        reservation.setStatus(EReservation.CANCELED);

        reservation.setCancelDate(LocalDateTime.now());

        // Create in-app notification for users

        String messageForTheUser = "HI " + reservation.getUser().getName()+ " your reservation have been Canceled  , To  :   " + reservation.getStartDate()  + " , for your : " + reservation.getEventTitle() ;
        notificationService.createNotification("Reservation Notification",messageForTheUser, reservation.getUser());


        Reservation updatedReservation = reservationService.updateReservation(reservation);
        return ResponseEntity.ok(updatedReservation);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<List<Reservation>> getReservationsByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Reservation> reservations = reservationService.findReservationsByDateRange(startDate, endDate);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<List<Reservation>> getUpcomingReservations(@RequestParam("limit") int limit) {
        List<Reservation> reservations = reservationService.findUpcomingReservations(limit);
        return ResponseEntity.ok(reservations);
    }



}
