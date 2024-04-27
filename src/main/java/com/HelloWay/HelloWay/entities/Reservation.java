package com.HelloWay.HelloWay.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
@Table(	name = "reservations")
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReservation ;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EReservation status = EReservation.NOT_YET ;


    @Column(name="eventTitle")
    private String eventTitle;

    @NotNull
    @Column
    private int numberOfGuests ;

    @NotNull
    @Column(length = 20)
    private LocalDateTime bookingDate ;


    @Column()
    private LocalDateTime cancelDate ;

    @NotNull
    @Column()
    private LocalDateTime startDate ;


    @Column()
    private LocalDateTime endDate ;


    @Column()
    private LocalDateTime confirmedDate ;

    @NotBlank
    @Column
    private String description;



    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_space")
    private Space space ;

    @OneToMany(mappedBy = "reservation")
    List<Board> boards ;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_user")
    private User user ;

    public Reservation(EReservation status,
                       String eventTitle,
                       int numberOfGuests,
                       LocalDateTime bookingDate,
                       LocalDateTime cancelDate,
                       LocalDateTime startDate,
                       LocalDateTime confirmedDate,
                       String description) {
        this.status = status;
        this.eventTitle = eventTitle;
        this.numberOfGuests = numberOfGuests;
        this.bookingDate = bookingDate;
        this.cancelDate = cancelDate;
        this.startDate = startDate;
        this.confirmedDate = confirmedDate;
        this.description = description;
    }

    public Reservation(EReservation status,
                       String eventTitle,
                       int numberOfGuests,
                       LocalDateTime bookingDate,
                       LocalDateTime cancelDate,
                       LocalDateTime startDate,
                       LocalDateTime confirmedDate,
                       String description,
                       Space space,
                       List<Board> boards,
                       User user) {
        this.status = status;
        this.eventTitle = eventTitle;
        this.numberOfGuests = numberOfGuests;
        this.bookingDate = bookingDate;
        this.cancelDate = cancelDate;
        this.startDate = startDate;
        this.confirmedDate = confirmedDate;
        this.description = description;
        this.space = space;
        this.boards = boards;
        this.user = user;
    }

    public Long getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(Long idReservation) {
        this.idReservation = idReservation;
    }

    public EReservation getStatus() {
        return status;
    }

    public void setStatus(EReservation status) {
        this.status = status;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalDateTime getCancelDate() {
        return cancelDate;
    }

    public void setCancelDate(LocalDateTime cancelDate) {
        this.cancelDate = cancelDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getConfirmedDate() {
        return confirmedDate;
    }

    public void setConfirmedDate(LocalDateTime confirmedDate) {
        this.confirmedDate = confirmedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public List<Board> getBoards() {
        return boards;
    }

    public void setBoards(List<Board> boards) {
        this.boards = boards;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}
