package com.HelloWay.HelloWay.payload.response;

import com.HelloWay.HelloWay.entities.Command;

public class Command_NumTableDTO {
    private Command command;
    private int numTable;

    public Command_NumTableDTO(Command command, int numTable) {
        this.command = command;
        this.numTable = numTable;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public int getNumTable() {
        return numTable;
    }

    public void setNumTable(int numTable) {
        this.numTable = numTable;
    }
}
