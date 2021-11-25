package net.atopecode.javalinvue.auth.service.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.javalin.http.Context;
import net.atopecode.javalinvue.auth.context.IUserPrincipal;
import net.atopecode.javalinvue.auth.context.IUserPrincipalToken;
import net.atopecode.javalinvue.auth.service.authentication.exception.InvalidAuthCredentialsException;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface IAuthenticationService {

    String generateAuthToken(String userName, String password) throws InvalidAuthCredentialsException, JsonProcessingException;

    void logout(Context ctx);

    void authenticateHttpRequest(Context ctx) throws JsonProcessingException;

    Optional<IUserPrincipalToken> getUserPrincipalFromToken(String token) throws JsonProcessingException;

    Optional<IUserPrincipal> getUserPrincipalFromContext(Context ctx);

}
