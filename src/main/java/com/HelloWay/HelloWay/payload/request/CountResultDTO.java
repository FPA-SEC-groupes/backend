package com.HelloWay.HelloWay.payload.request;


public class CountResultDTO {
    private Long key;
    private Long count;

    public CountResultDTO(Long key, Long count) {
        this.key = key;
        this.count = count;
    }

    public Long getKey() {
        return key;
    }

    public Long getCount() {
        return count;
    }
}
