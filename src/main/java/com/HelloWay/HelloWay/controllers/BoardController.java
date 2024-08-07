package com.HelloWay.HelloWay.controllers;

import com.HelloWay.HelloWay.entities.*;
import com.HelloWay.HelloWay.payload.response.MessageResponse;
import com.HelloWay.HelloWay.repos.BasketRepository;
import com.HelloWay.HelloWay.repos.BoardRepository;
import com.HelloWay.HelloWay.repos.RoleRepository;
import com.HelloWay.HelloWay.repos.UserRepository;
import com.HelloWay.HelloWay.services.BoardService;
import com.HelloWay.HelloWay.services.ZoneService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.HelloWay.HelloWay.entities.ERole.ROLE_GUEST;

@RestController
@RequestMapping("/api/boards")
public class BoardController {
    BoardService boardService;
    ZoneService zoneService;

    @Autowired
    PasswordEncoder encoder;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BasketRepository basketRepository;
    @Autowired
    public BoardController( BoardService boardService,ZoneService zoneService) {

        this.boardService = boardService;
        this.zoneService = zoneService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('PROVIDER')")
    @ResponseBody
    public Board addNewBoard(@RequestBody Board board) {
        return boardService.addNewBoard(board);
    }

    @JsonIgnore
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    @ResponseBody
    public List<Board> allBoards(){
        return boardService.findAllBoards();
    }


    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    @ResponseBody
    public Board findBoardById(@PathVariable("id") long id){
        return boardService.findBoardById(id);
    }


    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    @ResponseBody
    public Board updateBoard(@RequestBody Board board){
      return   boardService.updateBoard(board); }

    @PutMapping("/update/{boardId}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    @ResponseBody
    public ResponseEntity<?> updateBoard(@RequestBody Board board, @PathVariable long boardId){
        Board exestingBoard = boardService.findBoardById(boardId);
        Zone zone = exestingBoard.getZone();
        List<Board> zoneBoards = zone.getBoards();
        zoneBoards.remove(exestingBoard);
        for (Board b : zoneBoards){
            if (b.getNumTable() == board.getNumTable()){
                return ResponseEntity.badRequest().body("board exist with this num please try with an other");
            }
        }
        return ResponseEntity.ok().body(boardService.updateBoard(board));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Board> updateActivatedStatus(
            @PathVariable Long id, @RequestParam boolean activated) {
        Optional<Board> updatedBoard = boardService.updateActivatedStatus(id, activated);
        return updatedBoard.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    public ResponseEntity<?> deleteBoard(Long id, boolean step) {
        Board board = boardRepository.findById(id).orElse(null);
        if (board != null) {
            if (!step) {
                // First step: Disassociate baskets from the board
                if (board.getBaskets() != null) {
                    for (Basket basket : board.getBaskets()) {
                        basket.setBoard(null);
                        basketRepository.save(basket); // Save the updated basket
                    }
                }
                // Mark the board as processed
                boardRepository.save(board);
    
                // Create response for first step
                Map<String, String> response = new HashMap<>();
                response.put("status", "step1");
                response.put("message", "Baskets disassociated. Call again to delete the board.");
    
                return ResponseEntity.ok(response);
            } else {
                // Second step: Delete the board
                boardRepository.delete(board);
    
                // Create response for second step
                Map<String, String> response = new HashMap<>();
                response.put("status", "delete");
                response.put("message", "Board deleted successfully.");
    
                return ResponseEntity.ok(response);
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Board not found");
        }
    }



    // exist exeption for num table
    @PostMapping("/add/id_zone/{id_zone}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    @ResponseBody
    public ResponseEntity<?> addNewBoardByIdZone(@RequestBody Board board, @PathVariable Long id_zone) {
        if (boardService.boardExistsByNumInZone(board, id_zone)){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: num table title is already taken! in this Zone"));
        }
        Zone zone = zoneService.findZoneById(id_zone);
        Board boardObj =  boardService.addBoardByIdZone(board, id_zone);
        // User user = new User("Board"+boardObj.getIdTable()
        //         ,"Temp",
        //         zone.getSpace().getId_space().toString(),
        //         LocalDate.now(),
        //         null,
        //         "email"+boardObj.getIdTable()+LocalDate.now()+"@HelloWay.com"
        //         ,encoder.encode("Pass"+boardObj.getIdTable()+"*"+id_zone));
        // Set<Role> roles = new HashSet<>();
        // Role guestRole = roleRepository.findByName(ROLE_GUEST)
        //         .orElseThrow(() -> new RuntimeException("Error: Role Guest is not found."));
        // roles.add(guestRole);
        // user.setRoles(roles);
        // userRepository.save(user);
        return ResponseEntity.ok().body(boardObj);
    }

    @GetMapping("/id_zone/{id_zone}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    @ResponseBody
    public List<Board> getBoardsByIdZone( @PathVariable Long id_zone) {
        return boardService.getBoardsByIdZone( id_zone);
    }


}
