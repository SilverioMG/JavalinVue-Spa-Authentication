package net.atopecode.javalinvue.dto;

import io.javalin.http.Context;
import io.javalin.http.HttpCode;

/**
 * Esta clase se utiliza para envíar una respuesta estandarizada hacia el frontend en todas las peticiones Http.
 */
public class HttpResponse<TResult> {

    private TResult result;
    private boolean ok;
    private String message;

    public HttpResponse(TResult result, boolean ok, String message) {
        this.result = result;
        this.ok = ok;
        this.message = message;
    }

    public HttpResponse(Exception ex) {
        this.result = null;
        this.ok = false;
        this.message = ex.getMessage();
    }

    public TResult getResult() {
        return result;
    }

    public void setResult(TResult result) {
        this.result = result;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Context handleResult(Context ctx, HttpCode httpCode) {
        return ctx.json(this).status(httpCode);
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "result=" + result +
                ", ok=" + ok +
                ", message='" + message + '\'' +
                '}';
    }
}
