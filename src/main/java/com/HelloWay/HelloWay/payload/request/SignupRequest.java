package com.HelloWay.HelloWay.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

public class SignupRequest {

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;
    private String name;
    private String lastname;

    private LocalDate birthday;
    private String phone;
    private LocalDate hiring_date;
    private LocalDate contract_end_date;
    private Long salaire ;

    private String password ;

    private  String email ;
    private Set<String> role;

    public SignupRequest(String username, String name, String lastname, LocalDate birthday, String phone, LocalDate hiring_date, LocalDate contract_end_date, Long salaire, String password, String email, Set<String> role) {
        this.username = username;
        this.name = name;
        this.lastname = lastname;
        this.birthday = birthday;
        this.phone = phone;
        this.hiring_date = hiring_date;
        this.contract_end_date = contract_end_date;
        this.salaire = salaire;
        this.password = password;
        this.email = email;
        this.role = role;
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

    public LocalDate getHiring_date() {
        return hiring_date;
    }

    public void setHiring_date(LocalDate hiring_date) {
        this.hiring_date = hiring_date;
    }

    public LocalDate getContract_end_date() {
        return contract_end_date;
    }

    public void setContract_end_date(LocalDate contract_end_date) {
        this.contract_end_date = contract_end_date;
    }

    public Long getSalaire() {
        return salaire;
    }

    public void setSalaire(Long salaire) {
        this.salaire = salaire;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRole() {
        return this.role;
    }

    public void setRole(Set<String> role) {
        this.role = role;
    }
}
