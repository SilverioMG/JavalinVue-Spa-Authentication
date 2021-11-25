package net.atopecode.javalinvue.auth.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.javalin.http.Context;
import net.atopecode.javalinvue.auth.service.authentication.IAuthenticationService;

/**
 * Esta clase se utiliza como Interceptor (Javalin Before Handler) para obtener el token JWT enviado desde el frontend
 * en cada petición Http cuando el usuario está autenticado y añadir su info (UserPrincipal) al context.
 *
 */
public class AuthInterceptor {

    private IAuthenticationService authenticationService;

    public AuthInterceptor(IAuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public void handle(Context ctx) throws JsonProcessingException {
        authenticationService.authenticateHttpRequest(ctx);
    }


}
