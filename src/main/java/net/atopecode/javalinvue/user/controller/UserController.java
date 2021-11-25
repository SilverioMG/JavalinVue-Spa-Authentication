package net.atopecode.javalinvue.user.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpCode;
import io.javalin.http.NotFoundResponse;
import net.atopecode.javalinvue.dto.HttpResponse;
import net.atopecode.javalinvue.user.repository.IUserRepository;
import net.atopecode.javalinvue.user.model.User;

import java.util.Set;
import java.util.stream.Collectors;

public class UserController {

    private IUserRepository userRepository;

    public UserController(IUserRepository userRepository){
        this.userRepository = userRepository;
    }

    public void getAll(Context ctx) {
        Set<User> users = userRepository.getUsers().stream()
                .map((User user) -> {
                    User userWithoutDetails = new User(user);
                    userWithoutDetails.setUserDetails(null);
                    //userWithoutDetails.setPassword(null); //TODO... Descomentar por seguridad, se deja así para visualizar el password en el frontend.
                    return userWithoutDetails;
                })
                .collect(Collectors.toSet());

        HttpResponse response = new HttpResponse(users, true, "");
        response.handleResult(ctx, HttpCode.OK);
    }

    public void getOne(Context ctx){
        String idPathParam = ctx.pathParam("user-id");
        int id = Integer.parseInt(idPathParam);
        User user = userRepository.getUsers().stream()
                .filter(u -> (u.getId() == id))
                .findFirst()
                .map(u -> new User(u))
                .orElseThrow(() -> new NotFoundResponse());
        user.setPassword(null);

        HttpResponse response = new HttpResponse(user, true, "");
        response.handleResult(ctx, HttpCode.OK);
    }
}
