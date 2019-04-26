package com.expedia.homeaway.service.network;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PlacesRetrofitException extends RuntimeException {

    private final String url;
    private final Response response;
    private final Type type;
    private final Retrofit retrofit;
    PlacesRetrofitException(String message, String url, Response response, Type type, Throwable exception, Retrofit retrofit) {
        super(message, exception);
        this.url = url;
        this.response = response;
        this.type = type;
        this.retrofit = retrofit;
    }

    PlacesRetrofitException(String message, String url, Response response, Type type, Retrofit retrofit) {
        super(message);
        this.url = url;
        this.response = response;
        this.type = type;
        this.retrofit = retrofit;
    }

    public static PlacesRetrofitException httpError(String url, Response response, Retrofit retrofit) {
        String responseMessage = (response.message()) == null ? "Message not available" : response.message();
        String message = response.code() + " " + responseMessage;
        return new PlacesRetrofitException(message, url, response, Type.HTTP, retrofit);
    }

    public static PlacesRetrofitException unexpectedError(Throwable exception) {
        return new PlacesRetrofitException(exception.getMessage(), null, null, Type.UNEXPECTED, exception, null);
    }

    public static PlacesRetrofitException networkError(IOException exception) {
        return new PlacesRetrofitException(exception.getMessage(), null, null, Type.NETWORK, exception, null);
    }

    public static PlacesRetrofitException unauthorizedError(Throwable exception) {
        return new PlacesRetrofitException(exception.getMessage(), null, null, Type.UNAUTHORIZED, exception, null);
    }

    public String getUrl() {
        return url;
    }

    public Response getResponse() {
        return response;
    }

    public Type getType() {
        return type;
    }

    public <T> T getErrorBodyAs(Class<T> type) throws IOException {
        if (response == null || response.errorBody() == null) {
            return null;
        }
        Converter<ResponseBody, T> converter = retrofit.responseBodyConverter(type, new Annotation[0]);
        return converter.convert(response.errorBody());
    }

    public int getHttpResponseCode() {
        if (response == null) {
            return 0;
        }
        return response.code();
    }

    public enum Type {
        NETWORK,
        HTTP,
        UNAUTHORIZED,
        UNEXPECTED
    }
}