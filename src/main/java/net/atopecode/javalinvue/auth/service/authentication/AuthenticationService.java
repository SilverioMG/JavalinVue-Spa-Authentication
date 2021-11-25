package net.atopecode.javalinvue.auth.service.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.javalin.http.Context;
import net.atopecode.javalinvue.auth.context.IUserPrincipal;
import net.atopecode.javalinvue.auth.context.IUserPrincipalToken;
import net.atopecode.javalinvue.auth.context.UserPrincipal;
import net.atopecode.javalinvue.auth.service.authentication.exception.InvalidAuthCredentialsException;
import net.atopecode.javalinvue.auth.token.JwtPayload;
import net.atopecode.javalinvue.auth.token.JwtTokenProvider;
import net.atopecode.javalinvue.user.model.User;
import net.atopecode.javalinvue.user.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Esta clase realiza las operaciones necesarias para la Autenticación y Autorización del Usuario en el Servicio Web.
 */
public class AuthenticationService implements IAuthenticationService {

    public static final String AUTHORIZATION_HTTP_HEADER = "Authorization";
    public static final String TOKEN_QUERY_PARAM = "token";
    public final static String USER_LOGGED_CONTEXT_ATTRIBUTE_TAG = "userLogged";

    private IUserService userService;
    private JwtTokenProvider jwtTokenProvider;

    public AuthenticationService(IUserService userService,
                                 JwtTokenProvider jwtTokenProvider){
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Este método realiza la Autenticación.
     * Se obtiene el Usuario a partir de sus credenciales (si son correctas).
     * Se genera el token JWT a partir del Usuario.
     * @param userName
     * @param password
     * @return
     * @throws InvalidAuthCredentialsException
     * @throws JsonProcessingException
     */
    @Override
    public String generateAuthToken(String userName, String password) throws InvalidAuthCredentialsException, JsonProcessingException {
        if(StringUtils.isBlank(userName) || StringUtils.isBlank(password)){
            throw new InvalidAuthCredentialsException("Invalid authentication user credentials.");
        }

        User user = userService.findByNameAndPassword(userName, password).orElse(null);
        if(user == null){
            throw new InvalidAuthCredentialsException("Invalid authentication user credentials.");
        }

        IUserPrincipal userPrincipal = getUserPrincipalFromUser(user);
        String token = jwtTokenProvider.generateToken(userPrincipal);

        return token;
    }

    private IUserPrincipal getUserPrincipalFromUser(@NotNull User user){
        return new UserPrincipal(Integer.toString(user.getId()), user.getName(), null, user.getRoles());
    }

    /**
     * Se elimina al usuario del context de la petición Http para indicar que ya no hay nadie logueado.
     * @param ctx
     */
    @Override
    public void logout(Context ctx){
        ctx.attribute(USER_LOGGED_CONTEXT_ATTRIBUTE_TAG, null);
    }

    /**
     * Se obtiene el token JWT a partir de la petición Http y se valida.
     * Si el token JWT existe y es correcto, a partir de su info se crea el UserPrincipal y se
     * añade al 'context' de la petición Http para poder utilizarlo en el 'AccessManager'
     * (autorización de endpoints) u otros métodos del servicio web.
     *
     * Para las peticiones Http contra la API se recibe el JWT en el Header Http 'Authorization'.
     * Para las peticiones Http que enrutan Javalin Vue Views se recibe como 'query param'.
     * @param ctx
     */
    @Override
    public void authenticateHttpRequest(Context ctx) throws JsonProcessingException {
        IUserPrincipal userPrincipal = null;
        String token = getAuthTokenFromHttpHeader(ctx)
                .or(() -> getAuthTokenFromQueryParams(ctx))
                .orElse(null);

        if(token != null){
            boolean loggedIn = jwtTokenProvider.validateToken(token);
            if(loggedIn){
                userPrincipal = getUserPrincipalFromToken(token).orElse(null);
            }
        }

        //Se añade el Usuario al contexto de la petición Http.
        ctx.attribute(USER_LOGGED_CONTEXT_ATTRIBUTE_TAG, userPrincipal);
    }

    /**
     * Se obtiene el token del usuario autenticado a partir del Header 'Authorization' de la petición Http.
     * Se utiliza este método para las peticiones a la API.
     * @param ctx
     * @return
     */
    private Optional<String> getAuthTokenFromHttpHeader(@NotNull Context ctx){
        String token = null;
        String authHeader = ctx.header(AUTHORIZATION_HTTP_HEADER);
        if(StringUtils.isNotBlank(authHeader)){
            token = authHeader.replace("Bearer ", "");
        }

        return Optional.ofNullable(token);
    }

    /**
     * Se obtiene el token del usuario autenticado a partir de los Query Params de la petición Http.
     * Se utiliza este método para las peticiones de routing para las Javalin Vue Views.
     * @param ctx
     * @return
     */
    private Optional<String> getAuthTokenFromQueryParams(@NotNull Context ctx){
        String token = ctx.queryParam(TOKEN_QUERY_PARAM);
        return Optional.ofNullable(token);
    }

    @Override
    public Optional<IUserPrincipalToken> getUserPrincipalFromToken(String token) throws JsonProcessingException {
        if(StringUtils.isBlank(token)){
            return Optional.empty();
        }

        JwtPayload jwtPayload = jwtTokenProvider.getPayloadFromJWT(token);
        IUserPrincipalToken userPrincipal = new UserPrincipal(jwtPayload.getSub(), jwtPayload.getUserName(), token, jwtPayload.getRoles());
        return Optional.of(userPrincipal);
    }

    /**
     * Se recupera la info del usuario autenticado (si existe) del 'context' de la petición Http.
     * @param ctx
     * @return
     */
    @Override
    public Optional<IUserPrincipal> getUserPrincipalFromContext(@NotNull Context ctx){
        IUserPrincipal userPrincipal = ctx.attribute(USER_LOGGED_CONTEXT_ATTRIBUTE_TAG);
        return Optional.ofNullable(userPrincipal);
    }
}
