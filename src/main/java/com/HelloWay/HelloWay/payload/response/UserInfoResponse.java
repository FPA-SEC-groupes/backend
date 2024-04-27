package com.HelloWay.HelloWay.payload.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Data
public class UserInfoResponse {
    private Long id;
    private String name;
    private String lastname;
    private LocalDate birthday;
    private  String phone;
    private String username;
    private String email;
    private List<String> roles;
    private String sessionId;

    public UserInfoResponse(Long id, String name, String lastname, LocalDate birthday, String phone, String username, String email, List<String> roles, String sessionId) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.birthday = birthday;
        this.phone = phone;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.sessionId = sessionId;
    }

    public UserInfoResponse(Long id, String name, String lastname, LocalDate birthday, String phone, String username, String email, List<String> roles) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.birthday = birthday;
        this.phone = phone ;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
