package com.HelloWay.HelloWay.payload.response;

import com.HelloWay.HelloWay.entities.BasketProduct;
import com.HelloWay.HelloWay.entities.Command;

import java.util.List;

public class Command_NumTableDTO {
    private Command command;
    private Integer numTable; 
    private List<BasketProduct> basketProducts;

    // Constructor
    public Command_NumTableDTO(Command command, Integer numTable, List<BasketProduct> basketProducts) {
        this.command = command;
        this.numTable = numTable;
        this.basketProducts = basketProducts;
    }

    // Getters and Setters
    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public Integer getNumTable() {
        return numTable;
    }

    public void setNumTable(Integer numTable) {
        this.numTable = numTable;
    }

    public List<BasketProduct> getBasketProducts() {
        return basketProducts;
    }

    public void setBasketProducts(List<BasketProduct> basketProducts) {
        this.basketProducts = basketProducts;
    }
}
