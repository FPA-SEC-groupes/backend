package com.HelloWay.HelloWay.services;

import com.HelloWay.HelloWay.entities.Basket;
import com.HelloWay.HelloWay.entities.Board;
import com.HelloWay.HelloWay.entities.User;
import com.HelloWay.HelloWay.entities.Zone;
import com.HelloWay.HelloWay.exception.ResourceNotFoundException;
import com.HelloWay.HelloWay.repos.BasketProductRepository;
import com.HelloWay.HelloWay.repos.UserRepository;
import com.HelloWay.HelloWay.repos.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ZoneService {
    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private UserService userService;

    @Autowired
    BoardService boardService;

    @Autowired
    BasketProductRepository basketProductRepository;

    public Zone addZone(Zone zone){
        return zoneRepository.save(zone);
    }
    public List<Zone> findAllZones() {
        return zoneRepository.findAll();
    }

    public Zone updateZone(Zone updatedZone) {
        Zone existingZone = zoneRepository.findById(updatedZone.getIdZone()).orElse(null);
        if (existingZone != null) {
            // Copy the properties from the updatedZone to the existingZone
            existingZone.setZoneTitle(updatedZone.getZoneTitle());
            zoneRepository.save(existingZone);
            return existingZone;
        } else {
            // Handle the case where the zone doesn't exist in the database
            // You may throw an exception or handle it based on your use case.
            return null;
        }
    }
    public Zone findZoneById(Long id) {
        return zoneRepository.findById(id)
                .orElse(null);
    }

    @Transactional
    public void deleteZone(Long id) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with this id: " + id));

        // Disassociate related servers
        List<User> servers = new ArrayList<>(zone.getServers());
        for (User user : servers) {
            user.setZone(null);
            userService.updateUser(user);
        }

        // Disassociate related boards and baskets
        List<Board> boards = new ArrayList<>(zone.getBoards());
        for (Board board : boards) {
            for (Basket basket : board.getBaskets()) {
                basketProductRepository.deleteAllBasketProductByBasket(basket);
            }
            board.removeBaskets();
            board.setZone(null); // Assuming there is a zone attribute in the Board entity
            boardService.updateBoard(board);
        }

        // Now you can safely delete the Zone entity
        zoneRepository.deleteById(id);
    }


    public List<User> getServersByZone(Zone zone){
        return  zone.getServers();
    }


}
