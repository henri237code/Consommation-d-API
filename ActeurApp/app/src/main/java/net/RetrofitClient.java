package net;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class RetrofitClient {
    private static Retrofit retrofit;
    public static Retrofit get(String base){
        if (retrofit == null){
            HttpLoggingInterceptor log = new HttpLoggingInterceptor();
            log.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(log).build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(base)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client).build();
        }
        return retrofit;
    }
}
