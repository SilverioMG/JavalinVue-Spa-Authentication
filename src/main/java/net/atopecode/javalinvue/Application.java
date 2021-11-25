package net.atopecode.javalinvue;

import io.javalin.Javalin;
import io.javalin.core.security.RouteRole;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpCode;
import io.javalin.http.staticfiles.Location;
import io.javalin.plugin.rendering.vue.JavalinVue;
import io.javalin.plugin.rendering.vue.VueComponent;
import net.atopecode.javalinvue.auth.controller.AuthController;
import net.atopecode.javalinvue.auth.service.authentication.exception.InvalidAuthCredentialsException;
import net.atopecode.javalinvue.auth.service.authorization.IAuthorizationService;
import net.atopecode.javalinvue.dto.HttpResponse;
import net.atopecode.javalinvue.user.controller.UserController;
import net.atopecode.javalinvue.ioc.BeanSingletonFactory;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Application {
    public final static int serverPort = 8000;

    public enum Roles implements RouteRole { USER, ADMIN };

    //Dependency Injection:
    private static BeanSingletonFactory beans = new BeanSingletonFactory();

    public static void main(String[] args){
        Javalin app =Javalin.create(config -> {
            config.enableCorsForAllOrigins();
            config.enableWebjars();
            config.accessManager(Application::configAccessManager);
            config.addStaticFiles("/public", Location.CLASSPATH); //Los archivos dentro de 'resources/public/' se acceden desde el Html con el path '/'.
        }).start(serverPort);

        //Handler que se ejecuta antes de cada petición Http para recuperar el token del usuario autenticado y añadir
        //su info como 'UserPrincipal' al 'context' de la petición.
        app.before(beans.getAuthInterceptor()::handle);

        //StateFunction para la vista Javalin Vue que se ejecuta después del handler 'AuthInterceptor' y del 'AccessManager'
        //para envíar la info del usuario autenticado (UserPrincipal) a la vista Javalin Vue.
        JavalinVue.stateFunction = Application::configJavalinVueStateFunction;

        //VIEW Routes:
        app.get("/", (Context ctx) -> { ctx.redirect("/users", HttpServletResponse.SC_MOVED_PERMANENTLY);});
        app.get("/users", new VueComponent("user-overview"));
        app.get("/users/{user-id}", new VueComponent("user-profile"), Roles.USER);
        app.error(HttpCode.NOT_FOUND.getStatus(), "html", new VueComponent("not-found"));
        app.error(HttpCode.UNAUTHORIZED.getStatus(), "html", (ctx) -> ctx.redirect("/", HttpServletResponse.SC_MOVED_PERMANENTLY));
        app.error(HttpCode.FORBIDDEN.getStatus(),"html", (ctx) -> ctx.redirect("/", HttpServletResponse.SC_MOVED_PERMANENTLY));

        //API Routes:
        app.post("/api/auth", authController()::auth);
        app.get("/api/users", userController()::getAll);
        app.get("/api/users/{user-id}", userController()::getOne, Roles.USER);

        //Exceptions:
        app.exception(InvalidAuthCredentialsException.class,
                (ex, ctx) -> new HttpResponse<Exception>(ex).handleResult(ctx, HttpCode.BAD_REQUEST));

    }

    /**
     * Este método realiza la Autorización del Usuario al acceder a cada endpoint del Servicio Web.
     * Se ejecuta por cada petición Http (tanto para routing de Vistas como peticiones contra la Api) antes del correspondiente Handler del Controller.
     * @param handler
     * @param ctx
     * @param routeRoles
     * @throws Exception
     */
    private static void configAccessManager(@NotNull Handler handler, @NotNull Context ctx, @NotNull Set<RouteRole> routeRoles) throws Exception {
        if((routeRoles == null) || (routeRoles.isEmpty())) {
            //Si el endpoint no está protegido con ningún 'Rol' se procesa la petición Http sin problemas, no hay que realizar Autenticación.
            handler.handle(ctx);
            return;
        }

        IAuthorizationService authorizationService = beans.getAuthorizationService();
        boolean userAuthorized = authorizationService.isUserContextAuthorized(ctx, routeRoles);
        if(userAuthorized){
            handler.handle(ctx);
            return;
        }

        ctx.status(401).result("Unauthorized");
    }

    /**
     * Server Side State para Vue Views/Components.
     * Se envían al cliente web (JavalinVue) los valores necesarios para utilizar en el FrontEnd.
     * Se ejecuta justo antes de renderizar la vista para enviar al cliente web y después del 'access manager'.
     * No se ejecuta para llamadas a la API REST, solo para las peticiones que devuelven una Vista/Componente Vue.
     * @param ctx
     * @return
     */
    private static Map<String, Object> configJavalinVueStateFunction(Context ctx){
        Map<String, Object> viewStateMap = new HashMap<>();

        beans.getAuthViewStateService().addAuthUserInfoToViewState(ctx, viewStateMap);

        return viewStateMap;
    }

    //Controllers:
    private static AuthController authController() { return beans.getAuthController(); }
    private static UserController userController(){
        return beans.getUserController();
    }
}
