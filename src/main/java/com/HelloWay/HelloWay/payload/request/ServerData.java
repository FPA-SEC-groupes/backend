package com.HelloWay.HelloWay.payload.request;

import java.time.LocalDate;

public class ServerData {
    private String serverName;
    private LocalDate date;
    private String command;
    private double sum;

    public ServerData(String serverName, LocalDate date, String command, double sum) {
        this.serverName = serverName;
        this.date = date;
        this.command = command;
        this.sum = sum;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }
}
