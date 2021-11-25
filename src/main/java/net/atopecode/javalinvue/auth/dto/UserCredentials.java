package net.atopecode.javalinvue.auth.dto;

public class UserCredentials {

    private String name;
    private String password;

    public UserCredentials(){
        this.name = null;
        this.password = null;
    }

    public UserCredentials(String name, String password){
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserCredentials{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
