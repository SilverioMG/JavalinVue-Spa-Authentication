package net.atopecode.javalinvue.user.service;

import net.atopecode.javalinvue.user.model.User;

import java.util.Optional;

public interface IUserService {

    Optional<User> findByName(String name);
    Optional<User> findByNameAndPassword(String name, String password);
}
