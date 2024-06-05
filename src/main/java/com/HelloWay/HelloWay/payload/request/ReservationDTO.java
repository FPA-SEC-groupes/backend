package com.HelloWay.HelloWay.payload.request;


import com.HelloWay.HelloWay.entities.Board;
import com.HelloWay.HelloWay.entities.EReservation;
import com.HelloWay.HelloWay.entities.Reservation;
import com.HelloWay.HelloWay.entities.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReservationDTO {

    private Long idReservation;
    private EReservation status;
    private String eventTitle;
    private int numberOfGuests;
    private LocalDateTime bookingDate;
    private LocalDateTime cancelDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime confirmedDate;
    private String description;
    private User user;
    private List<Board> boards;

    public ReservationDTO(Reservation reservation) {
        this.idReservation = reservation.getIdReservation();
        this.status = reservation.getStatus();
        this.eventTitle = reservation.getEventTitle();
        this.numberOfGuests = reservation.getNumberOfGuests();
        this.bookingDate = reservation.getBookingDate();
        this.cancelDate = reservation.getCancelDate();
        this.startDate = reservation.getStartDate();
        this.endDate = reservation.getEndDate();
        this.confirmedDate = reservation.getConfirmedDate();
        this.description = reservation.getDescription();
        this.user = reservation.getUser();
        this.boards = reservation.getBoards();
    }

   
}
