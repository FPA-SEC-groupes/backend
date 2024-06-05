package com.HelloWay.HelloWay.payload.request;

import com.HelloWay.HelloWay.entities.Restrictions;
import com.HelloWay.HelloWay.entities.User;

import lombok.Data;

@Data
public class RestrictionsDTO {

    private Long id;
    private String description;
    private int numberOfRestrictions;
    private Long userId;
    
    public RestrictionsDTO(Long id, String description, int numberOfRestrictions, Long userId) {
        this.id = id;
        this.description = description;
        this.numberOfRestrictions = numberOfRestrictions;
        this.userId = userId;
    }

    public static RestrictionsDTO from(Restrictions restrictions) {
        return new RestrictionsDTO(
                restrictions.getId(),
                restrictions.getDescription(),
                restrictions.getNumberOfRestrictions(),
                restrictions.getUser().getId()
        );
    }

    public Restrictions toEntity() {
        Restrictions restrictions = new Restrictions();
        restrictions.setId(id);
        restrictions.setDescription(description);
        restrictions.setNumberOfRestrictions(numberOfRestrictions);
        User user = new User();
        user.setId(userId);
        restrictions.setUser(user);
        return restrictions;
    }
}