package net.atopecode.javalinvue.auth.service.viewstate;

import io.javalin.http.Context;

import java.util.Map;

public interface IAuthViewStateService {

    void addAuthUserInfoToViewState(Context ctx, Map<String, Object> viewStateMap);
}
