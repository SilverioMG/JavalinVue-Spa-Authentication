package net.atopecode.javalinvue.auth.context;

import net.atopecode.javalinvue.Application;

import java.util.List;

public interface IUserPrincipal {

    String getId();
    String getName();
    List<Application.Roles> getRoles();
}
