package toplab18.runcoffee.Network;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static Context context;

    private static final String BASE_URL = "http://5.d.reanima.store/rest/auth-session/";

    private static OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new AddCookiesInterceptor(context))
            .addInterceptor(new ReceivedCookiesInterceptor(context))
            .build();

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();



    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create());



    public static <S> S createService(Class<S> serviceClass) {

        Retrofit retrofit = builder
                .client(httpClient.build())
                .build();


        return retrofit.create(serviceClass);
    }




}