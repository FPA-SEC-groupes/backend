package com.HelloWay.HelloWay.payload.response;

import com.HelloWay.HelloWay.entities.Command;

public class Command_NumTableDTO {
    private Command command;
    private Integer numTable; // Use Integer instead of int

    public Command_NumTableDTO(Command command, Integer numTable) {
        this.command = command;
        this.numTable = numTable;
    }

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
}
