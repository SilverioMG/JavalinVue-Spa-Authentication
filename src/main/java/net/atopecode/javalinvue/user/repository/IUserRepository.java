package net.atopecode.javalinvue.user.repository;

import net.atopecode.javalinvue.user.model.User;

import java.util.Set;

public interface IUserRepository {

    Set<User> getUsers();
}
