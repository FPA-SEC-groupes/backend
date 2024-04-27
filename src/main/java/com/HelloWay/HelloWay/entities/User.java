package com.HelloWay.HelloWay.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Setter
@Getter
@ToString
@AllArgsConstructor
public  class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name ;

    // @Column(nullable = false)
    private String lastname ;

    //   @Column(nullable = false)
    private LocalDate birthday ;

    //  @Column(nullable = false)
    private String phone ;
    private String email ;

    //  @Column(nullable = false)
    private String username ;

    //  @Column(nullable = false)
    private String password ;

    private boolean activated;

    @OneToOne(cascade = CascadeType.ALL)
    Image image;

    @OneToMany(mappedBy="user")
    private List<Reclamation> reclamation;

    @OneToMany(mappedBy="user")
    private List<Command> commands;

    @OneToMany(mappedBy="server" )
    private List<Command> server_commands;

    @OneToMany(mappedBy="user")
    private List<Board> boards;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_space")
    private Space space;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_space_for_server")
    private Space serversSpace;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;

    @OneToOne
    private Space moderatorSpace ;

    @JsonIgnore
    @OneToMany(mappedBy="user")
    private List<Reservation> reservations;


    @JsonIgnore
    @OneToMany(mappedBy="recipient")
    private List<Notification> notifications;

    public List<Command> getServer_commands() {
        return server_commands;
    }

    public void setServer_commands(List<Command> server_commands) {
        this.server_commands = server_commands;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<Reclamation> getReclamation() {
        return reclamation;
    }

    public void setReclamation(List<Reclamation> reclamation) {
        this.reclamation = reclamation;
    }

    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

    public List<Board> getBoards() {
        return boards;
    }

    public void setBoards(List<Board> boards) {
        this.boards = boards;
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public Space getServersSpace() {
        return serversSpace;
    }

    public void setServersSpace(Space serversSpace) {
        this.serversSpace = serversSpace;
    }

    public Space getModeratorSpace() {
        return moderatorSpace;
    }

    public void setModeratorSpace(Space moderatorSpace) {
        this.moderatorSpace = moderatorSpace;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();



    public User(String username, String name, String lastname, LocalDate birthday, String phone, String email, String password ) {
        this.username = username;
        this.name = name;
        this.lastname = lastname;
        this.birthday = birthday;
        this.phone = phone;
        this.email = email;
        this.password = password;}


    public User() {

    }

    public Set<Role> getRoles() {
        return roles;
    }

    public User(String name,
                String lastname,
                LocalDate birthday,
                String phone,
                String email,
                String username,
                String password,
                boolean activated) {
        this.name = name;
        this.lastname = lastname;
        this.birthday = birthday;
        this.phone = phone;
        this.email = email;
        this.username = username;
        this.password = password;
        this.activated = activated;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}
