package com.HelloWay.HelloWay.services;

import com.HelloWay.HelloWay.entities.*;
import com.HelloWay.HelloWay.payload.response.QuantitysProduct;
import com.HelloWay.HelloWay.repos.CommandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.HelloWay.HelloWay.entities.Status.*;

@Service
public class CommandService {

     private  Map<String, String> lastServerWithBoardIdForCommand = new HashMap<>();


     @Autowired
     UserService userService ;

     @Autowired
    CommandRepository commandRepository ;

    @Autowired
    BasketService basketService;

    @Autowired
    BasketProductService basketProductService;

    public List<Command> findAllCommands() {
        return commandRepository.findAll();
    }

    public Command updateCommand(Command updatedCommand) {
        Command existingCommand = commandRepository.findById(updatedCommand.getIdCommand()).orElse(null);
        if (existingCommand != null) {
            // Copy the properties from the updatedCommand to the existingCommand
            existingCommand.setStatus(updatedCommand.getStatus());
            existingCommand.setTimestamp(updatedCommand.getTimestamp());
            existingCommand.setSum(updatedCommand.getSum());
            existingCommand.setUser(updatedCommand.getUser());
            existingCommand.setServer(updatedCommand.getServer());

            commandRepository.save(existingCommand);
            return existingCommand;
        } else {
            // Handle the case where the command doesn't exist in the database
            // You may throw an exception or handle it based on your use case.
            return null;
        }
    }    public Command createCommand(Command command) {
        command.setTimestamp(LocalDateTime.now());
        return commandRepository.save(command);
    }

    public Command findCommandById(Long id) {
        return commandRepository.findById(id)
                .orElse(null);
    }

    public void deleteCommand(Long id) {
        commandRepository.deleteById(id);
    }

    public void acceptCommand(Long commandId) {
        Command command = findCommandById(commandId);
        // Logic to accept the command and process it accordingly
        // For example, update the status of the command or trigger external operations
        command.setStatus(CONFIRMED);
        commandRepository.save(command);
    }

    public void payCommand(Long commandId) {
        Command command = findCommandById(commandId);
        // Logic to accept the command and process it accordingly
        // For example, update the status of the command or trigger external operations
        command.setStatus(PAYED);
        command.setSum(CalculateSum(command));
        commandRepository.save(command);
    }

    public void refuseCommand(Long commandId) {
        Command command = findCommandById(commandId);
        // Logic to refuse the command
        // For example, update the status of the command or trigger external operations
        command.setStatus(REFUSED);
        commandRepository.save(command);
    }

    public Command createCommandForBasket(Long basketId, Command command) {
        Basket basket = basketService.findBasketById(basketId);
        command.setBasket(basket);
        return commandRepository.save(command);
    }

    public void setServerForCommand(Long commandId, User server) {
        Command command = findCommandById(commandId);
        Zone zone = command.getBasket().getBoard().getZone();
        List<User> servers = new ArrayList<>();
        servers = zone.getServers();
        List<Command> commands = new ArrayList<>();
        if (servers.contains(server)){
            command.setServer(server);
            commandRepository.save(command);
            commands = server.getCommands();
            commands.add(command);
            userService.updateUser(server);
            lastServerWithBoardIdForCommand.put(zone.getIdZone().toString(),server.getId().toString());
        }

    }

    public double CalculateSum(Command command) {
        double result = 0;
        Basket basket = command.getBasket();
        Map<Product, QuantitysProduct> products_Quantity = basketProductService.getProductsQuantityByBasketId(basket.getId_basket());

        for (Product product : products_Quantity.keySet()) {
            boolean hasActivePromotion = false;
            for (Promotion promotion : product.getPromotions()) {
                if (promotion.getStartDate().isBefore(LocalDateTime.now())
                        && promotion.getEndDate().isAfter(LocalDateTime.now())) {
                    result += (product.getPrice() * (1 - (promotion.getPercentage() / 100.0))) * products_Quantity.get(product).getOldQuantity();
                    hasActivePromotion = true;
                }
            }

            if (!hasActivePromotion) {
                result += product.getPrice() * products_Quantity.get(product).getOldQuantity();
            }
        }

        return result;
    }


    public Map<String, String> getLastServerWithBoardIdForCommand() {
        return lastServerWithBoardIdForCommand;
    }

    public void setLastServerWithBoardIdForCommand(Map<String, String> lastServerWithBoardIdForCommand) {
        this.lastServerWithBoardIdForCommand = lastServerWithBoardIdForCommand;
    }

    public List<Command> getServerCommands(User server){

        LocalTime currentTime = LocalTime.now();
        List<Command> serverCommands = server.getServer_commands();
        List<Command> actualServerCommand = new ArrayList<>();
        for (Command command : serverCommands){
            if (command.getStatus().equals(NOT_YET) || command.getStatus().equals(CONFIRMED)
                    && command.getTimestamp().getHour() < currentTime.getHour() + 13){
                actualServerCommand.add(command);
            }
        }
    return actualServerCommand;
    }

    public List<Command> getServerPayedCommandsPerDay(User server, LocalDate localDate){

        List<Command> serverCommands = server.getServer_commands();
        List<Command> perDayServerCommand = new ArrayList<>();
        for (Command command : serverCommands){
            if (command.getStatus().equals(PAYED)
                    && command.getTimestamp().toLocalDate().equals(localDate)){
                perDayServerCommand.add(command);
            }
        }
        return perDayServerCommand;
    }

    public double getServerSumCommandsPerDay(User server,LocalDate localDate){
        double result = 0;
        List<Command> perDayServerCommand = getServerPayedCommandsPerDay(server, localDate);
        for (Command command : perDayServerCommand){
            result += CalculateSum(command);
        }
        return result;
    }

    public double getServerSumCommandsPerMonth(User server, YearMonth yearMonth) {
        double result = 0;
        int daysInMonth = yearMonth.lengthOfMonth();

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = yearMonth.atDay(day);
            result += getServerSumCommandsPerDay(server, date);
        }

        return result;
    }

    public int getServerCommandsCountPerDay(User server,LocalDate localDate){
        List<Command> perDayServerCommand = getServerPayedCommandsPerDay(server, localDate);
        return perDayServerCommand.size();
    }

}
