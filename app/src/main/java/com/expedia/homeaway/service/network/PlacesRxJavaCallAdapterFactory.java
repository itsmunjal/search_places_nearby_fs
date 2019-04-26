package com.expedia.homeaway.service.network;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.Result;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.functions.Func1;

public class PlacesRxJavaCallAdapterFactory extends CallAdapter.Factory {
    private final RxJavaCallAdapterFactory original;

    private PlacesRxJavaCallAdapterFactory() {
        original = RxJavaCallAdapterFactory.create();
    }

    public static CallAdapter.Factory create() {
        return new PlacesRxJavaCallAdapterFactory();
    }

    @Override
    public CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        return new RxCallAdapterWrapper(retrofit, original.get(returnType, annotations, retrofit));
    }

    private static class RxCallAdapterWrapper implements CallAdapter<Observable<?>> {
        private final Retrofit retrofit;
        private final CallAdapter<?> wrapped;

        public RxCallAdapterWrapper(Retrofit retrofit, CallAdapter<?> wrapped) {
            this.retrofit = retrofit;
            this.wrapped = wrapped;
        }

        @Override
        public Type responseType() {
            return wrapped.responseType();
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R> Observable<Result<R>> adapt(Call<R> call) {
            return ((Observable) wrapped.adapt(call))
                    .flatMap(new Func1<Result<R>, Observable<Result<R>>>() {
                        @Override
                        public Observable<Result<R>> call(Result<R> result) {
                            Response<R> response = result.response();
                            if (response != null && response.isSuccessful()) {
                                return Observable.just(result);
                            } else {
                                if (result.isError()) {
                                    if (result.error() instanceof IOException) {
                                        return Observable.error(PlacesRetrofitException.networkError((IOException) result.error()));
                                    } else {
                                        return Observable.error(PlacesRetrofitException.unexpectedError(result.error()));
                                    }
                                } else {
                                    return Observable.error(PlacesRetrofitException.unexpectedError(new Throwable("Something went wrong. Please try again later.")));
                                }
                            }
                        }
                    });
        }
    }
}