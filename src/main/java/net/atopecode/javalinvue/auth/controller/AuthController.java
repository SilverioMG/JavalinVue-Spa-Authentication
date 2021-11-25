package net.atopecode.javalinvue.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.javalin.http.Context;
import io.javalin.http.HttpCode;
import net.atopecode.javalinvue.auth.context.IUserPrincipalToken;
import net.atopecode.javalinvue.auth.dto.UserCredentials;
import net.atopecode.javalinvue.auth.service.authentication.IAuthenticationService;
import net.atopecode.javalinvue.auth.service.authentication.exception.InvalidAuthCredentialsException;
import net.atopecode.javalinvue.dto.HttpResponse;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.util.StringUtil;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class AuthController {

    private IAuthenticationService authService;

    public AuthController(IAuthenticationService authService){
        this.authService = authService;
    }

    /**
     * Si las credenciales del Usuario que se quiere autenticar son correctas, se devuelve
     * como respuesta de la petición Http la info del usuario con el token generado a partir de la autenticación.
     * @return
     */
    public void auth(Context ctx) throws JsonProcessingException, InvalidAuthCredentialsException {
        Optional<UserCredentials> credentials = Optional.ofNullable(ctx.bodyAsClass(UserCredentials.class));
        String userName = credentials
                            .map(c -> c.getName())
                            .orElse(null);
        String password = credentials
                            .map(c -> c.getPassword())
                            .orElse(null);

        String token = authService.generateAuthToken(userName, password);
        IUserPrincipalToken userPrincipal = authService.getUserPrincipalFromToken(token).orElse(null);
        HttpResponse response = new HttpResponse(userPrincipal, true, "User Authenticated.");
        response.handleResult(ctx, HttpCode.OK);
    }

    /**
     * Se elimina la info del Usuario del context de la petición Http para indicar que ya no hay nadie logueado.
     * Se redirecciona a la url recibida como parámetro (y no se enviará ninguna info del usuario logueado a la vista frontend).
     * @param ctx
     */
    public void logout(Context ctx, String urlViewRedirect){
        authService.logout(ctx);
        if(StringUtil.isNotBlank(urlViewRedirect))
        {
            ctx.redirect(urlViewRedirect, HttpServletResponse.SC_MOVED_PERMANENTLY);
        }
        else{
            ctx.status(HttpCode.INTERNAL_SERVER_ERROR);
        }
    }
}
