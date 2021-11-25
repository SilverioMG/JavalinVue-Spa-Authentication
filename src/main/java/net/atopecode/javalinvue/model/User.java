package net.atopecode.javalinvue.model;

public class User {

    private int id;
    private String name;
    private String email;
    private UserDetails userDetails;

    public User(int id, String name, String email, UserDetails userDetails){
        this.id = id;
        this.name = name;
        this.email = email;
        this.userDetails = userDetails;
    }

    public User(User otherUser){
        this.id = otherUser.id;
        this.name = otherUser.name;
        this.email = otherUser.email;
        this.userDetails = new UserDetails(otherUser.getUserDetails());
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", userDetails=" + userDetails +
                '}';
    }
}
