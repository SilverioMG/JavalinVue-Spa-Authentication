package net.atopecode.javalinvue.auth.service.viewstate;

import io.javalin.http.Context;
import net.atopecode.javalinvue.auth.context.IUserPrincipal;
import net.atopecode.javalinvue.auth.interceptor.AuthInterceptor;
import net.atopecode.javalinvue.auth.service.authentication.AuthenticationService;
import net.atopecode.javalinvue.auth.service.authentication.IAuthenticationService;

import java.util.Map;

/**
 * Este servicio se encarga de pasar al frontend (Vista/Componentes Javalin Vue) la info del Usuario autenticado guardado
 * en el 'context' de la petición Http.
 *
 * Desde el frontend se debe utilizar la info de Usuario autenticado para enviar en cada petición el token (JWT)
 * hacia el server donde se hará la correspondiente Autorización para el correspondiente endpoint (routing de Vista o
 * petición a la Api).
 */
public class AuthViewStateService implements IAuthViewStateService{

    public static final String USER_LOGGED_VIEW_STATE_TAG = AuthenticationService.USER_LOGGED_CONTEXT_ATTRIBUTE_TAG;

    private IAuthenticationService authenticationService;

    public AuthViewStateService(IAuthenticationService authenticationService){
        this.authenticationService = authenticationService;
    }

    /**
     *  Se envían la info del Usuario logueado (si existe) al frontend junto con el token de autenticación para se pueda utilizar
     *  desde el frontend en las peticiones Http hacia la API y en las peticiones Http de Vista Vue.
     * @param ctx
     * @param viewStateMap
     */
    @Override
    public void addAuthUserInfoToViewState(Context ctx, Map<String, Object> viewStateMap){
        if((ctx == null) || (viewStateMap == null)){
            return;
        }

        IUserPrincipal userPrincipal = authenticationService.getUserPrincipalFromContext(ctx).orElse(null);
        viewStateMap.put(USER_LOGGED_VIEW_STATE_TAG, userPrincipal);
    }
}
