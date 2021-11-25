package net.atopecode.javalinvue.auth.service.authorization;

import io.javalin.core.security.RouteRole;
import io.javalin.http.Context;
import net.atopecode.javalinvue.auth.context.IUserPrincipal;

import java.util.Optional;
import java.util.Set;

public interface IAuthorizationService {

    boolean isUserContextAuthorized(Context ctx, Set<RouteRole> routeRoles);
}
