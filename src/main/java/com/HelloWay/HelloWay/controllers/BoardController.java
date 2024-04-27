package com.HelloWay.HelloWay.controllers;

import com.HelloWay.HelloWay.entities.*;
import com.HelloWay.HelloWay.payload.response.MessageResponse;
import com.HelloWay.HelloWay.repos.RoleRepository;
import com.HelloWay.HelloWay.repos.UserRepository;
import com.HelloWay.HelloWay.services.BoardService;
import com.HelloWay.HelloWay.services.ZoneService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
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

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    @ResponseBody
    public void deleteBoard(@PathVariable("id") long id){
        boardService.deleteBoard(id); }


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
        User user = new User("Board"+boardObj.getIdTable()
                ,"Temp",
                zone.getSpace().getId_space().toString(),
                LocalDate.now(),
                null,
                "email"+boardObj.getIdTable()+LocalDate.now()+"@HelloWay.com"
                ,encoder.encode("Pass"+boardObj.getIdTable()+"*"+id_zone));
        Set<Role> roles = new HashSet<>();
        Role guestRole = roleRepository.findByName(ROLE_GUEST)
                .orElseThrow(() -> new RuntimeException("Error: Role Guest is not found."));
        roles.add(guestRole);
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok().body(boardObj);
    }

    @GetMapping("/id_zone/{id_zone}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    @ResponseBody
    public List<Board> getBoardsByIdZone( @PathVariable Long id_zone) {
        return boardService.getBoardsByIdZone( id_zone);
    }


}
