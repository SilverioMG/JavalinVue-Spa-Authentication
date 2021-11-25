package net.atopecode.javalinvue.auth.service.authorization;

import io.javalin.core.security.RouteRole;
import io.javalin.http.Context;
import net.atopecode.javalinvue.auth.context.IUserPrincipal;
import net.atopecode.javalinvue.auth.interceptor.AuthInterceptor;
import net.atopecode.javalinvue.auth.service.authentication.IAuthenticationService;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

public class AuthorizationService implements IAuthorizationService {

    private IAuthenticationService authenticationService;

    public AuthorizationService(IAuthenticationService authenticationService){
        this.authenticationService = authenticationService;
    }

    /**
     * Se realiza la 'Autorización' de un endpoint a partir de los 'Roles' que pueden acceder a él y la info del
     * 'Usuario' autenticado (UserPrincipal en el context de la petición Http).
     * @param ctx
     * @param routeRoles
     * @return 'false' si no hay info de 'Usuario' autenticado en el 'context' de la petición http.
     *         'false' si hay info de 'Usuario' autenticado en el 'context' de la petición http pero no tiene el 'Rol' necesario para acceder a él.
     *         'true' si hay info de 'Usuario' autenticado en el 'context' de la petición http y el endpoint no está protegido por ningún 'Rol'.
     *         'true' info de 'Usuario' autenticado en el 'context' de la petición http y además tiene el 'Rol' necesario para acceder al endpoint.
     */
    @Override
    public boolean isUserContextAuthorized(Context ctx, Set<RouteRole> routeRoles) {
        boolean isAuthorized = false;

        IUserPrincipal userPrincipal = authenticationService.getUserPrincipalFromContext(ctx).orElse(null);
        if(userPrincipal != null){
            //El Usuario está autenticado porque se recibió 'token' en la petición Http y se añadió su info como 'UserPrincipal' al context de la petición Http.
            if((routeRoles == null) || (routeRoles.isEmpty())) {
                //Si el endpoint no está protegido, el usuario está autorizado para acceder a él.
                isAuthorized = true;
            }
            else {
                //Como el endpoint está protegido, se realiza la autorización. Se comprueba que el usuario tenga el rol necesario para seguir con la petición.
                isAuthorized = userPrincipal.getRoles().stream()
                        .anyMatch(routeRole -> userPrincipal.getRoles().contains(routeRole));
            }
        }

        return isAuthorized;
    }
}
