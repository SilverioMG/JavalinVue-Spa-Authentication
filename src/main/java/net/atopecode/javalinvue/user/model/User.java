package net.atopecode.javalinvue.user.model;

import net.atopecode.javalinvue.Application;

import java.util.List;

public class User {

    private int id;
    private String name;
    private String password;
    private String email;
    private UserDetails userDetails;
    private List<Application.Roles> roles;

    public User(int id, String name, String password, String email, UserDetails userDetails, List<Application.Roles> roles){
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.userDetails = userDetails;
        this.roles = roles;
    }

    public User(User otherUser){
        this.id = otherUser.id;
        this.name = otherUser.name;
        this.password = otherUser.password;
        this.email = otherUser.email;
        this.userDetails = new UserDetails(otherUser.getUserDetails());
        this.roles = otherUser.roles;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword () { return password; }

    public String getEmail() {
        return email;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) { this.password = password; }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public List<Application.Roles> getRoles(){
        return this.roles;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", userDetails=" + userDetails +
                ", roles=" + roles +
                '}';
    }
}
