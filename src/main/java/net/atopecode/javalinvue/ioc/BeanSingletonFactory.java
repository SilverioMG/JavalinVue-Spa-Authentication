package net.atopecode.javalinvue.ioc;

import net.atopecode.javalinvue.controller.UserController;
import net.atopecode.javalinvue.model.IUserRepository;
import net.atopecode.javalinvue.model.UserRepository;

public class BeanSingletonFactory {

    private IUserRepository userRepository = null;
    private UserController userController = null;

    public BeanSingletonFactory(){
        //Empty Constructor.
    }

    public IUserRepository getUserRepository(){
        if(userRepository == null){
            userRepository = new UserRepository();
        }

        return userRepository;
    }

    public UserController getUserController() {
        if (userController == null) {
            userController = new UserController(getUserRepository());
        }

        return userController;
    }
}
