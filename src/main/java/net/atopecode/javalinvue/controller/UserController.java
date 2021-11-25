package net.atopecode.javalinvue.controller;

import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import net.atopecode.javalinvue.model.IUserRepository;
import net.atopecode.javalinvue.model.User;
import net.atopecode.javalinvue.model.UserRepository;

import java.util.Optional;
import java.util.stream.Collectors;

public class UserController {

    private IUserRepository userRepository;

    public UserController(IUserRepository userRepository){
        this.userRepository = userRepository;
    }

    public void getAll(Context ctx){
        ctx.json(userRepository.getUsers().stream()
                .map((User user) -> {
                    User userWithoutDetails = new User(user);
                    userWithoutDetails.setUserDetails(null);
                    return userWithoutDetails;
                })
                .collect(Collectors.toSet())
        );
    }

    public void getOne(Context ctx){
        String idPathParam = ctx.pathParam("user-id");
        int id = Integer.parseInt(idPathParam);
        User user = userRepository.getUsers().stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundResponse());
        ctx.json(user);
    }
}
