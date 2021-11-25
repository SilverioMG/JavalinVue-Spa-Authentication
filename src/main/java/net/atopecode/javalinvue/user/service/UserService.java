package net.atopecode.javalinvue.user.service;

import net.atopecode.javalinvue.user.model.User;
import net.atopecode.javalinvue.user.repository.IUserRepository;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.Set;

public class UserService implements IUserService {

    private IUserRepository userRepository;

    public UserService(IUserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByName(final String name) {
        Optional<User> user = Optional.empty();
        if(StringUtils.isBlank(name)){
            return user;
        }

        user = userRepository.getUsers().stream()
                .filter(u -> StringUtils.equals(name.trim().toLowerCase(), u.getName().trim().toLowerCase()))
                .findFirst();

        return user;
    }

    @Override
    public Optional<User> findByNameAndPassword(String name, String password) {
        Optional<User> user = Optional.empty();
        if(StringUtils.isBlank(name) || StringUtils.isBlank(password)){
            return user;
        }

        Set<User> users = userRepository.getUsers();
        user = users.stream()
                .filter(u -> (StringUtils.equals(name.trim().toLowerCase(), u.getName().trim().toLowerCase()))
                                && (StringUtils.equals(password, u.getPassword())))
                .findFirst();

        return user;
    }
}
