package net.atopecode.javalinvue.ioc;

import net.atopecode.javalinvue.auth.controller.AuthController;
import net.atopecode.javalinvue.auth.interceptor.AuthInterceptor;
import net.atopecode.javalinvue.auth.service.authentication.AuthenticationService;
import net.atopecode.javalinvue.auth.service.authentication.IAuthenticationService;
import net.atopecode.javalinvue.auth.service.authorization.AuthorizationService;
import net.atopecode.javalinvue.auth.service.authorization.IAuthorizationService;
import net.atopecode.javalinvue.auth.service.viewstate.AuthViewStateService;
import net.atopecode.javalinvue.auth.service.viewstate.IAuthViewStateService;
import net.atopecode.javalinvue.auth.token.JwtTokenProvider;
import net.atopecode.javalinvue.user.controller.UserController;
import net.atopecode.javalinvue.user.repository.IUserRepository;
import net.atopecode.javalinvue.user.repository.UserRepository;
import net.atopecode.javalinvue.user.service.IUserService;
import net.atopecode.javalinvue.user.service.UserService;

/**
 * Esta clase se utiliza como Inyector de Dependencias.
 * Se define la creación de cada Bean (emulando Spring Beans) y las dependencias entre ellos.
 * Cada Bean e un objeto Singleton para reutilizar en cualquier parte del código.
 * En vez de utilizar anotaciones (@Autowired, @Bean...) como en SpringFramework que necesitan utilizar 'Reflexión',
 * se delcaran los Beans de forma manual. Si hay que modificar algún Bean hay que hacerlo a mano en esta clase o heredando de
 * ella y modificando los métodos necesarios.
 */
public class BeanSingletonFactory {

    private IUserRepository userRepository = null;
    private IUserService userService = null;

    private AuthController authController = null;
    private UserController userController = null;

    private JwtTokenProvider jwtTokenProvider = null;
    private IAuthenticationService authenticationService = null;
    private IAuthorizationService authorizationService = null;
    private AuthInterceptor authInterceptor = null;
    private IAuthViewStateService authViewStateService;


    public BeanSingletonFactory(){
        //Empty Constructor.
    }

    public IUserRepository getUserRepository(){
        if(userRepository == null){
            userRepository = new UserRepository();
        }

        return userRepository;
    }

    public IUserService getUserService(){
        if(userService == null){
            userService = new UserService(getUserRepository());
        }

        return userService;
    }

    public AuthController getAuthController() {
        if (authController == null) {
            authController = new AuthController(getAuthenticationService());
        }

        return authController;
    }

    public UserController getUserController() {
        if (userController == null) {
            userController = new UserController(getUserRepository());
        }

        return userController;
    }

    public JwtTokenProvider getJwtTokenProvider(){
        if(jwtTokenProvider == null){
            jwtTokenProvider = new JwtTokenProvider();
        }

        return jwtTokenProvider;
    }

    public IAuthenticationService getAuthenticationService(){
        if(authenticationService == null){
            authenticationService = new AuthenticationService(getUserService(), getJwtTokenProvider());
        }

        return authenticationService;
    }

    public IAuthorizationService getAuthorizationService(){
        if(authorizationService == null){
            authorizationService = new AuthorizationService(getAuthenticationService());
        }

        return authorizationService;
    }

    public AuthInterceptor getAuthInterceptor(){
        if(authInterceptor == null){
            authInterceptor = new AuthInterceptor(getAuthenticationService());
        }

        return authInterceptor;
    }

    public IAuthViewStateService getAuthViewStateService(){
        if(authViewStateService == null){
            authViewStateService =  new AuthViewStateService(getAuthenticationService());
        }

        return authViewStateService;
    }
}
