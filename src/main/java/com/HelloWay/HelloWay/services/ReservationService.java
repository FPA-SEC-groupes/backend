package com.HelloWay.HelloWay.services;

import com.HelloWay.HelloWay.entities.*;
import com.HelloWay.HelloWay.exception.ResourceNotFoundException;
import com.HelloWay.HelloWay.repos.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
     ReservationRepository reservationRepository;

    @Autowired
    SpaceService spaceService;

    @Autowired
    BoardService boardService;

    @Autowired
    private  UserService userService;

    @Autowired
    NotificationService notificationService;



    public List<Reservation> findAllReservations() {
        return reservationRepository.findAll();
    }


    public Reservation updateReservation(Reservation updatedReservation) {
        Reservation existingReservation = reservationRepository.findById(updatedReservation.getIdReservation()).orElse(null);
        if (existingReservation != null) {
            // Copy the properties from the updatedReservation to the existingReservation
            existingReservation.setStatus(updatedReservation.getStatus());
            existingReservation.setEventTitle(updatedReservation.getEventTitle());
            existingReservation.setNumberOfGuests(updatedReservation.getNumberOfGuests());
            existingReservation.setBookingDate(updatedReservation.getBookingDate());
            existingReservation.setCancelDate(updatedReservation.getCancelDate());
            existingReservation.setStartDate(updatedReservation.getStartDate());
            existingReservation.setEndDate(updatedReservation.getEndDate());
            existingReservation.setConfirmedDate(updatedReservation.getConfirmedDate());
            existingReservation.setDescription(updatedReservation.getDescription());

            reservationRepository.save(existingReservation);
            return existingReservation;
        } else {
            // Handle the case where the reservation doesn't exist in the database
            // You may throw an exception or handle it based on your use case.
            return null;
        }
    }
    public Reservation findReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElse(null);
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    public Reservation createReservationForSpaceAndUser(Reservation reservation,
                                                        Long spaceId,
                                                        Long userId){
        Space space = spaceService.findSpaceById(spaceId);
        User user = userService.findUserById(userId);
        reservation.setSpace(space);
        reservation.setUser(user);
        Reservation reservationObject = reservationRepository.save(reservation);

        List<Reservation> reservations = new ArrayList<>();
        reservations = space.getReservations();
        reservations.add(reservationObject);
        spaceService.updateSpace(space);

        List<Reservation> userReservations = new ArrayList<>();
        userReservations = user.getReservations();
        userReservations.add(reservationObject);
        userService.updateUser(user);

        // Create in-app notification for users
        String messageForTheModerator = "A new reservation has been made for your space: " + space.getTitleSpace() + " for  : " + reservation.getStartDate() + "by " + user.getName() + " with email : " +
                user.getEmail() + " , PhoneNumber : " + user.getPhone();
        String messageForTheUser = "Hello " + user.getName()+ " your reservation have been submitted successfully , you will be contacted by the Space :   " + space.getTitleSpace()  + " , PhoneNumber : " + space.getPhoneNumber();
        notificationService.createNotification("Reservation Notification", messageForTheModerator, space.getModerator());
        notificationService.createNotification("Reservation Notification",messageForTheUser, user);

        return reservationObject;
    }

    public List<Reservation> findReservationsBySpaceId(Long spaceId) {
        Space space = spaceService.findSpaceById(spaceId);
        if (space == null) {
            return new ArrayList<>();
        }
        return space.getReservations();
    }

    public List<Reservation> findReservationsByUserId(Long userId) {
        User user = userService.findUserById(userId);
        if (user == null) {
            return new ArrayList<>();
        }
        return user.getReservations();
    }

    public List<Reservation> findReservationsByDateRange(LocalDate startDate, LocalDate endDate) {
        return reservationRepository.findByStartDateBetween(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
    }

    public List<Reservation> findUpcomingReservations(int limit) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return reservationRepository.findByStartDateAfterOrderByStartDateAsc(currentDateTime)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }


    public Reservation createReservationForSpaceAndUserWithBoard(Reservation reservation, Long spaceId, Long userId, Long boardId) {
        Space space = spaceService.findSpaceById(spaceId);
        Board board = boardService.findBoardById(boardId);
        User user = userService.findUserById(userId);
        reservation.setSpace(space);
        reservation.setUser(user);

        List<Board> boards = new ArrayList<>();
        boards = reservation.getBoards();
        boards.add(board);
        reservation.setBoards(boards);


        Reservation reservationObject = reservationRepository.save(reservation);

        List<Reservation> reservations = new ArrayList<>();
        reservations = space.getReservations();
        reservations.add(reservationObject);
        spaceService.updateSpace(space);

        List<Reservation> userReservations = new ArrayList<>();
        userReservations = user.getReservations();
        userReservations.add(reservationObject);
        userService.updateUser(user);

        //List<Reservation> boardReservations = new ArrayList<>();
        //TODO : we must update the relation between board and reservation
        //boardReservations = board.getReservation()

        board.setReservation(reservationObject);
        boardService.updateBoard(board);

        // Create in-app notification for users
        String messageForTheModerator = "A new reservation has been made for your space: " + space.getTitleSpace() + " for  : " + reservation.getStartDate() + "by " + user.getName() + " with email : " +
                user.getEmail() + " , PhoneNumber : " + user.getPhone();
        String messageForTheUser = "Hello" + user.getName()+ " your reservation have been submitted successfully , you will be contacted by the Space :   " + space.getTitleSpace()  + " , PhoneNumber : " + space.getPhoneNumber();
        notificationService.createNotification("Reservation Notification", messageForTheModerator, space.getModerator());
        notificationService.createNotification("Reservation Notification",messageForTheUser, user);


        return reservationObject;
    }

    public Reservation assignReservationToTables(List<Long> tablesIds , Reservation reservation){
    List<Board> boards = new ArrayList<>();
    for (long i :tablesIds){
        Board board = boardService.findBoardById(i);
        if (board == null){
            throw new ResourceNotFoundException("board does not exist with this id :  " + i);
        }
        else {
            board.setReservation(reservation);
            boards.add(boardService.updateBoard(board));
        }
    }
    List<Board> reservationBoards = new ArrayList<>();
    reservationBoards = reservation.getBoards();
    reservationBoards.addAll(boards);
    reservation.setBoards(reservationBoards);
    return reservationRepository.save(reservation);
    }

    public List<Board> getTablesByDisponibilitiesDate(LocalDate date, long spaceId) {

        Space space = spaceService.findSpaceById(spaceId);

        List<Board> allBoards = new ArrayList<>();

        List<Zone> zones = space.getZones();
        for (Zone zone : zones){
            allBoards.addAll(zone.getBoards());
        }

        List<Board> availableTables = allBoards.stream()
                .filter(board -> isTableAvailable(board, date))
                .collect(Collectors.toList());

        return availableTables;
    }

    private boolean isTableAvailable(Board board, LocalDate date) {
        if (board.getReservation() == null) {
            return true;
        }

        LocalDate reservationDate = board.getReservation().getStartDate().toLocalDate();

        return !date.equals(reservationDate);
    }

}
