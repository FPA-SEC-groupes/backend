package com.HelloWay.HelloWay.payload;

public class Value {
    private String tableId;
    private String role;

    public Value(String tableId, String role) {
        this.tableId = tableId;
        this.role = role;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
