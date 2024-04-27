package com.HelloWay.HelloWay.services;

import com.HelloWay.HelloWay.entities.Board;
import com.HelloWay.HelloWay.entities.Zone;
import com.HelloWay.HelloWay.exception.ResourceNotFoundException;
import com.HelloWay.HelloWay.repos.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BoardService {
    @Autowired
    BoardRepository boardRepository ;

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

    public Board findBoardById(Long id) {
        return boardRepository.findById(id)
                .orElse(null);
    }

    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
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
