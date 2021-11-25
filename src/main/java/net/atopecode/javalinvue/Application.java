package net.atopecode.javalinvue;

import io.javalin.Javalin;
import io.javalin.plugin.rendering.vue.VueComponent;
import net.atopecode.javalinvue.controller.UserController;
import net.atopecode.javalinvue.ioc.BeanSingletonFactory;

public class Application {
    public final static int serverPort = 8000;

    //Dependency Injection:
    private static BeanSingletonFactory beans = new BeanSingletonFactory();

    public static void main(String[] args){
        Javalin app =Javalin.create(config -> {
            config.enableWebjars();
        }).start(serverPort);

        //VIEW Routes:
        //app.get("/", new VueComponent("hello-world"));
        app.get("/users", new VueComponent("user-overview"));
        app.get("/users/{user-id}", new VueComponent("user-profile"));
        app.error(404, "html", new VueComponent("not-found"));

        //API Routes:
        app.get("/api/users", userController()::getAll);
        app.get("/api/users/{user-id}", userController()::getOne);
    }

    private static UserController userController(){
        return beans.getUserController();
    }
}
