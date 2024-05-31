package com.HelloWay.HelloWay.services;

import com.HelloWay.HelloWay.entities.*;
import com.HelloWay.HelloWay.exception.ResourceNotFoundException;
import com.HelloWay.HelloWay.payload.request.SpaceCreationDTO;
import com.HelloWay.HelloWay.payload.request.WifiDTO;
import com.HelloWay.HelloWay.payload.request.SpaceCreationDTO.WifiInfo;
import com.HelloWay.HelloWay.payload.response.SpaceDTO;
import com.HelloWay.HelloWay.repos.SpaceRepository;
import com.HelloWay.HelloWay.repos.ZoneRepository;
import com.google.zxing.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.*;

import javax.persistence.EntityNotFoundException;

import static com.HelloWay.HelloWay.DistanceLogic.DistanceCalculator.calculateDistance;

@Service
public class SpaceService {

    @Autowired
    private SpaceRepository spaceRepository;

    @Autowired
    private    UserService userService;

    @Autowired
    private    CategorieService categorieService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private  ZoneRepository zoneRepository;

    @Autowired
    private WifiService wifiService;

    public List<Space> findAllSpaces() {
        return spaceRepository.findAll();
    }

    public Space updateSpace(Space updatedSpace) {
        Space existingSpace = spaceRepository.findById(updatedSpace.getId_space()).orElse(null);
        if (existingSpace != null) {
            // Copy the properties from the updatedSpace to the existingSpace
            existingSpace.setTitleSpace(updatedSpace.getTitleSpace());
            existingSpace.setLatitude(updatedSpace.getLatitude());
            existingSpace.setLongitude(updatedSpace.getLongitude());
            existingSpace.setPhoneNumber(updatedSpace.getPhoneNumber());
            existingSpace.setNumberOfRate(updatedSpace.getNumberOfRate());
            existingSpace.setDescription(updatedSpace.getDescription());
            existingSpace.setRating(updatedSpace.getRating());
            existingSpace.setSurfaceEnM2(updatedSpace.getSurfaceEnM2());

            spaceRepository.save(existingSpace);
            return existingSpace;
        } else {
            // Handle the case where the space doesn't exist in the database
            // You may throw an exception or handle it based on your use case.
            return null;
        }
    }
    public Space addNewSpace(Space space) throws IOException {

        return spaceRepository.save(space);
    }

    public Space findSpaceById(Long id) {
        return spaceRepository.findById(id)
                .orElse(null);
    }

    public void deleteSpace(Long id) {
        spaceRepository.deleteById(id);
    }

    public  Space addSpaceByIdModeratorAndIdSpaceCategory(Space space, Long idG, Long idSpaceCategorie){

        if (idSpaceCategorie==1){space.setSpaceCategorie(SpaceCategorie.Restaurant);}
        if (idSpaceCategorie==2){space.setSpaceCategorie(SpaceCategorie.Cafes);}
        if (idSpaceCategorie==3){space.setSpaceCategorie(SpaceCategorie.Bar);}

        Space spaceObject= new Space();
        spaceObject = space ;
        User user = userService.findUserById(idG);
        spaceObject.setModerator(user);

        spaceRepository.save(spaceObject);

        user.setModeratorSpace(spaceObject);
        userService.updateUser(user);

        return spaceObject;


    }

    public Space addSpaceByIdModeratorAndSpaceCategory(SpaceCreationDTO spaceDTO, Long idG, Long idSpaceCategorie) {
        Space spaceObject = new Space();
    
        // Set fields from DTO
        spaceObject.setTitleSpace(spaceDTO.getTitleSpace());
        spaceObject.setDescription(spaceDTO.getDescription());
        spaceObject.setLatitude(spaceDTO.getLatitude());
        spaceObject.setLongitude(spaceDTO.getLongitude());
        spaceObject.setValidation(spaceDTO.getValidation());
        spaceObject.setPhoneNumber(spaceDTO.getPhoneNumber());
        spaceObject.setNumberOfRate(spaceDTO.getNumberOfRate());
        spaceObject.setSurfaceEnM2(spaceDTO.getSurfaceEnM2());
    
        // Set space category based on idSpaceCategorie
        int categoryId = Math.toIntExact(idSpaceCategorie); // Safe if you control the input
        switch (categoryId) {
            case 1:
                spaceObject.setSpaceCategorie(SpaceCategorie.Restaurant);
                break;
            case 2:
                spaceObject.setSpaceCategorie(SpaceCategorie.Bar);
                break;
            case 3:
                spaceObject.setSpaceCategorie(SpaceCategorie.Cafes);
                break;
            default:
                throw new IllegalArgumentException("Invalid space category ID");
        }
    
        // Retrieve and set moderator
        User user = userService.findUserById(idG);
        // .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + idG));
        spaceObject.setModerator(user);
    
        // Save the Space object
        Space space = spaceRepository.save(spaceObject);
        if(!spaceDTO.getWifis().isEmpty()){
            WifiDTO wifiDTO = new WifiDTO();
            wifiDTO.setSpaceId(space.getId_space());
        
            // Map SpaceCreationDTO.WifiInfo to WifiDTO.WifiInfo
            List<WifiDTO.WifiInfo> wifiInfos = new ArrayList<>();
            for (SpaceCreationDTO.WifiInfo wifiInfo : spaceDTO.getWifis()) {
                WifiDTO.WifiInfo dtoWifiInfo = new WifiDTO.WifiInfo();
                dtoWifiInfo.setSsid(wifiInfo.getSsid());
                dtoWifiInfo.setPassword(wifiInfo.getPassword());
                wifiInfos.add(dtoWifiInfo);
            }
        
            wifiDTO.setWifis(wifiInfos);
            
            // Save WiFi information
            wifiService.saveWifis(wifiDTO);
        }
        // Create WifiDTO and set its fields
       
    
        // Update user with the space moderator role
        user.setModeratorSpace(spaceObject);
        userService.updateUser(user);
    
        return spaceObject;
    }
    
        public Space getSpaceByIdModerator(Long idModerator){

        User user = userService.findUserById(idModerator);
        return  user.getModeratorSpace();
    }

    public Space getSpaceByIdCategory(Long idCategory){

        Categorie categorie = categorieService.findCategorieById(idCategory);
        return  categorie.getSpace();
    }

    public List<Space> getSpacesByIdSpaceCategory(Long idCategory){

        List<Space> spaces = new ArrayList<Space>();
        List<Space> resSpaces = new ArrayList<Space>();
        spaces = spaceRepository.findAll();
        for (Space space:spaces){
            if (space.getSpaceCategorie().ordinal() == idCategory){
                resSpaces.add(space);
            }
        }

        return  resSpaces;
    }


    public void setServerInZone(Long spaceId, Long moderatorUserId, Long serverId, Long zoneId) throws NotFoundException {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Space not found"));

        User moderator = userService.findUserById(moderatorUserId);


        // Check if the user is the moderator of the space
        if (!space.getModerator().equals(moderator)) {
            throw new ResourceNotFoundException("User is not the moderator of the space");
        }

        User server = userService.findUserById(serverId);

        // Check if the user is the moderator of the space
        if (!space.getServers().contains(server)) {
            throw new ResourceNotFoundException("User is not a server in  the space");
        }

        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found"));

        // Update the server's zone
        server.setZone(zone);
        userService.addUser(server);
    }

    public void addServerInSpace(Long spaceId, Long moderatorUserId, Long serverId) throws NotFoundException {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Space not found"));

        User moderator = userService.findUserById(moderatorUserId);


        // Check if the user is the moderator of the space
        if (!space.getModerator().equals(moderator)) {
            throw new ResourceNotFoundException("User is not the moderator of the space");
        }

        User server = userService.findUserById(serverId);

        // Update the server's space
        server.setServersSpace(space);
       // userService.addUser(server);
        List<User> spaceServers = new ArrayList<>();
        spaceServers = space.getServers();
        spaceServers.add(server);
        space.setServers(spaceServers);
        spaceRepository.save(space);
    }

    public Page<Space> getSpaces(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return spaceRepository.findAll(pageable);
    }

    public void deleteServerFromZone(Long spaceId, Long moderatorUserId, Long serverId, Long zoneId) throws NotFoundException {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Space not found"));

        User moderator = userService.findUserById(moderatorUserId);


        // Check if the user is the moderator of the space
        if (!space.getModerator().equals(moderator)) {
            throw new ResourceNotFoundException("User is not the moderator of the space");
        }

        User server = userService.findUserById(serverId);

        // Check if the user is the moderator of the space
        if (!space.getServers().contains(server)) {
            throw new ResourceNotFoundException("User is not a server in  the space");
        }

        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found"));

        // Update the server's zone
        server.setZone(null);
        userService.addUser(server);
    }

    public List<User> getServersBySpace(Space space){
        return space.getServers();
    }
    public Space getSpaceByWaiterId(User waiter){return waiter.getServersSpace();}

    public Space addNewRate(Space space, Float newRate){

        long numberOfRate = space.getNumberOfRate() + 1;
        float rate = space.getRating();

        float totalRate = rate + newRate ;
        float result = totalRate / numberOfRate;
        space.setNumberOfRate(numberOfRate);
        space.setRating(result);
        return  spaceRepository.save(space);
    }

    public List<Space> getTheNearestSpacesByDistance(String userLatitude,
                                                        String userLongitude,
                                                        double threshold){
        Map<Space, Double> spaceDistanceMap = new HashMap<>();
        Map<Space, Double> sortedSpaceDistanceMap = new HashMap<>();
        List<Space> allSpaces = spaceRepository.findAll();
        List<Space> sortedSpaces = new ArrayList<>();
        for (Space space : allSpaces){
            double distance = calculateDistance(Double.parseDouble(userLatitude), Double.parseDouble(userLongitude),
                    Double.parseDouble(space.getLatitude()), Double.parseDouble(space.getLongitude()));
            if (distance <= threshold){
                spaceDistanceMap.put(space, distance);
            }
        }
        sortedSpaceDistanceMap = sortByValue(spaceDistanceMap);
        sortedSpaces.addAll(sortedSpaceDistanceMap.keySet());


        return sortedSpaces;
    }

    public static Map<Space, Double> sortByValue(Map<Space, Double> map) {
        List<Map.Entry<Space, Double>> list = new ArrayList<>(map.entrySet());

        // Sort the list of entries using a custom comparator for values
        Collections.sort(list, new Comparator<Map.Entry<Space, Double>>() {
            @Override
            public int compare(Map.Entry<Space, Double> o1, Map.Entry<Space, Double> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        // Create a LinkedHashMap to maintain the insertion order of sorted entries
        Map<Space, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Space, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
    public String getValidationById(Long id) {
        Optional<Space> space = spaceRepository.findById(id);
        if (space.isPresent()) {
            return space.get().getValidation();
        } else {
            throw new IllegalArgumentException("Space with id " + id + " not found");
        }
    }
    
}
