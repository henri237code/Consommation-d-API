package net;

import model.*;
import java.util.*;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {
    @GET("acteurs") Call<List<Actor>> getActors();
    @GET("total_acteurs") Call<Map<String,Integer>> getTotal();
    @POST("acteur/")
    Call<Actor> createActor(@Body Actor actor);
    @PUT("acteur/{id}") Call<Actor> update(@Path("id") int id, @Body ActorUpdate body);
    @DELETE("acteur/{id}") Call<Actor> deleteActor(@Path("id") int id);
}
