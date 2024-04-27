package com.HelloWay.HelloWay.payload.response;

public class InformationAfterScan {
    private String spaceId;
    private String tableId;

    private String sessionId;

    public InformationAfterScan(String spaceId, String tableId, String sessionId) {
        this.spaceId = spaceId;
        this.tableId = tableId;
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public InformationAfterScan(String spaceId, String tableId) {
        this.spaceId = spaceId;
        this.tableId = tableId;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }
}
