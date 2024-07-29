package com.HelloWay.HelloWay.services;

import com.HelloWay.HelloWay.entities.*;
import com.HelloWay.HelloWay.exception.ResourceNotFoundException;
import com.HelloWay.HelloWay.payload.request.ReservationDTO;
import com.HelloWay.HelloWay.repos.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

    @Autowired
    private MessageSource messageSource;


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
        // String messageForTheModerator = "A new reservation has been made for your space: " + space.getTitleSpace() + " for  : " + reservation.getStartDate() + "by " + user.getName() + " with email : " +
        //         user.getEmail() + " , PhoneNumber : " + user.getPhone();
        // String messageForTheUser = "Hello " + user.getName()+ " your reservation have been submitted successfully , you will be contacted by the Space :   " + space.getTitleSpace()  + " , PhoneNumber : " + space.getPhoneNumber();
        // Locale userLocale = new Locale(reservation.getUser().getPreferredLanguage());
        // String reservationTitle = messageSource.getMessage("reservationTitle", null, userLocale);
        // String moderatorTemplate = messageSource.getMessage("reservation.moderator", null, userLocale);
        // String formattedModeratorMessage = MessageFormat.format(moderatorTemplate,
        //         space.getTitleSpace(),
        //         reservation.getStartDate(),
        //         // format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
        //         user.getName(),
        //         user.getEmail(),
        //         user.getPhone()
        // );

        // // For the user
        // String userTemplate = messageSource.getMessage("reservation.user", null, userLocale);
        List<String>parames= new ArrayList<>();
        parames.add(0,String.valueOf(space.getTitleSpace()));
        parames.add(1,String.valueOf(reservation.getStartDate() ));
        parames.add(2,String.valueOf(user.getUsername()));
        parames.add(3,String.valueOf(user.getEmail()));
        parames.add(4,String.valueOf(user.getPhone()));
        List<String>paramesClient= new ArrayList<>();
        paramesClient.add(0,String.valueOf(user.getUsername()));
        paramesClient.add(1,String.valueOf(space.getTitleSpace()));
        paramesClient.add(2,String.valueOf(space.getPhoneNumber()));
        
        // String formattedUserMessage = MessageFormat.format(userTemplate,
        //         user.getName(),
        //         space.getTitleSpace(),
        //         space.getPhoneNumber()
        // );
        notificationService.createNotification("reservationTitle", "reservation.moderator",parames, space.getModerator());
        notificationService.createNotification("reservationTitle","reservation.user",paramesClient, user);

        return reservationObject;
    }

    public List<ReservationDTO> findReservationsBySpaceId(Long spaceId) {
        Space space = spaceService.findSpaceById(spaceId);
        List<Reservation> reservations = space.getReservations();
        return reservations.stream().map(ReservationDTO::new).collect(Collectors.toList());
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
        // Locale userLocale = new Locale(reservation.getUser().getPreferredLanguage());
        // Locale moderatorLocale = new Locale(space.getModerator().getPreferredLanguage());
        // String reservationTitle = messageSource.getMessage("reservationTitle", null, userLocale);
        // String moderatorTemplate = messageSource.getMessage("reservation.moderator1", null, moderatorLocale);
        List<String>parames= new ArrayList<>();
        List<String>paramesClient= new ArrayList<>();
        parames.add(0,String.valueOf(space.getTitleSpace()));
        parames.add(1,String.valueOf(reservation.getStartDate()));
        parames.add(2,String.valueOf(user.getName()));
        parames.add(3,String.valueOf(user.getEmail()));
        parames.add(3,String.valueOf(user.getPhone()));
        // String formattedModeratorMessage = MessageFormat.format(moderatorTemplate,
        //         space.getTitleSpace(),
        //         reservation.getStartDate(),
        //         // format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
        //         user.getName(),
        //         user.getEmail(),
        //         user.getPhone()
        // );
        // String userTemplate = messageSource.getMessage("reservation.user1", null, userLocale);
        notificationService.createNotification("reservationTitle", "reservation.moderator1",parames, space.getModerator());
        notificationService.createNotification("reservationTitle","reservation.user1",paramesClient, user);


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
        if (!board.isActivated()) {
            return false;
        }
        
        if (board.getReservation() == null) {
            return true;
        }
    
        LocalDate reservationDate = board.getReservation().getStartDate().toLocalDate();
    
        return !date.equals(reservationDate);
    }
    

}
