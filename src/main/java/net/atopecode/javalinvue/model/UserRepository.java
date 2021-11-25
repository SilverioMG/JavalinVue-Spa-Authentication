package net.atopecode.javalinvue.model;

import java.util.HashSet;
import java.util.Set;

public class UserRepository implements IUserRepository{

    private Set<User> users = new HashSet<User>();

    public UserRepository(){
        loadData();
    }

    private void loadData(){
        /* Old version for creating an inline Set object.
        this.users = new HashSet<User>(){{
            add(new User(1, "John", "john@fake.co", new UserDetails("21.02.1964", 2773d)));
            add(new User(2, "Mary", "mary@fake.co", new UserDetails("12.05.1994", 1222d)));
            add(new User(3, "Dave", "dave@fake.co", new UserDetails("01.05.1984", 1833d)));
        }};*/

        this.users = Set.of(
                new User(1, "John", "john@fake.co", new UserDetails("21.02.1964", 2773d)),
                new User(2, "Mary", "mary@fake.co", new UserDetails("12.05.1994", 1222d)),
                new User(3, "Dave", "dave@fake.co", new UserDetails("01.05.1984", 1833d))
        );
    }
    @Override
    public Set<User> getUsers(){
        return users;
    }

}
