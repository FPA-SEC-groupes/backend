package com.HelloWay.HelloWay.services;

import com.HelloWay.HelloWay.entities.Basket;
import com.HelloWay.HelloWay.entities.Board;
import com.HelloWay.HelloWay.entities.Zone;
import com.HelloWay.HelloWay.exception.ResourceNotFoundException;
import com.HelloWay.HelloWay.repos.BasketRepository;
import com.HelloWay.HelloWay.repos.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BoardService {
    @Autowired
    BoardRepository boardRepository ;
    
    @Autowired
    private BasketRepository basketRepository;
    
    @Autowired
    ZoneService zoneService;


    public Board addNewBoard(Board board){
        return boardRepository.save(board);
    }
    public List<Board> findAllBoards() {
        return boardRepository.findAll();
    }

    public Board updateBoard(Board updatedBoard) {
        Board existingBoard = boardRepository.findById(updatedBoard.getIdTable()).orElse(null);
        if (existingBoard != null) {
            // Copy the properties from the updatedBoard to the existingBoard
            existingBoard.setNumTable(updatedBoard.getNumTable());
            existingBoard.setAvailability(updatedBoard.isAvailability());
            existingBoard.setPlaceNumber(updatedBoard.getPlaceNumber());

             return  boardRepository.save(existingBoard);
        } else {
            // Handle the case where the board doesn't exist in the database
            // You may throw an exception or handle it based on your use case.
            throw new ResourceNotFoundException("Board not found");
        }
    }

    public Optional<Board> updateActivatedStatus(Long id, boolean activated) {
        Optional<Board> boardOptional = boardRepository.findById(id);
        if (boardOptional.isPresent()) {
            Board board = boardOptional.get();
            board.setActivated(activated);
            boardRepository.save(board);
            return Optional.of(board);
        }
        return Optional.empty();
    }
    public Board findBoardById(Long id) {
        return boardRepository.findById(id)
                .orElse(null);
    }

    public void deleteBoard(Long id) {
        Board board = boardRepository.findById(id).orElse(null);
        if (board != null) {
            // Ensure baskets are disassociated from the board
            if (board.getBaskets() != null) {
                for (Basket basket : board.getBaskets()) {
                    basket.setBoard(null);
                    basketRepository.save(basket); // Save the updated basket
                }
            }
            boardRepository.delete(board);
        }
    }
    
    

    public Board addBoardByIdZone(Board board, Long id_zone) {
         Board boardObject = new Board();
         boardObject = board;
        Zone zone = zoneService.findZoneById(id_zone);
        boardObject.setZone(zone);
        boardRepository.save(boardObject);
        List<Board> boards = new ArrayList<Board>();
        boards = zone.getBoards();
        boards.add(boardObject);
        zone.setBoards(boards);
        zoneService.updateZone(zone);
        return boardObject;

    }

    public Boolean boardExistsByNumInZone(Board board, Long idZone) {

        Boolean result = false;
        Zone zone = zoneService.findZoneById(idZone);
        List<Board> boards = new ArrayList<Board>();
        boards = zone.getBoards();
        for (Board boa : boards) {
            if (boa.getNumTable() == board.getNumTable()) {
                result = true;
            }
        }
        return result ;
    }

    public List<Board> getBoardsByIdZone(Long id_zone) {
        Zone zone = zoneService.findZoneById(id_zone);
        return  zone.getBoards();
    }


}
