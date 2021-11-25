package net.atopecode.javalinvue.auth.context;

import net.atopecode.javalinvue.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Info del Usuario autenticado que se añadirá al Context de cada petición Http.
 */
public class UserPrincipal implements IUserPrincipalToken {

    private String id;
    private String name;
    private String token;
    private List<Application.Roles> roles;

    public UserPrincipal(String id, String name, String token, List<Application.Roles> roles) {
        this.id = id;
        this.name = name;
        this.token = token;
        this.roles = (!roles.isEmpty()) ? roles : new ArrayList<>();
    }

    @Override
    public String getId(){
        return id;
    };

    public void setId(String id){
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public List<Application.Roles> getRoles() {
        return new ArrayList<Application.Roles>(roles);
    }

    @Override
    public String toString() {
        return "Principal{" +
                "name='" + name + '\'' +
                ", token='" + token + '\'' +
                ", roles=" + roles +
                '}';
    }
}
