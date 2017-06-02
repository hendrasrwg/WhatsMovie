package com.example.me.whatsmovie.network;

import com.example.me.whatsmovie.model.DetailResponse;
import com.example.me.whatsmovie.model.PopulerResponse;
import com.example.me.whatsmovie.modelReviews.ReviewsResponse;
import com.example.me.whatsmovie.modelTrailer.TrailerResponse;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Me on 5/21/2017.
 */

public interface ApiInterface {

    @GET("3/movie/{category}?&language=en-US&")
    Observable<PopulerResponse> getJsonPopuler(@Path("category") String category, @Query("api_key") String key, @Query("page") int page);

    @GET("3/movie/{id}?&language=en-US")
    Observable<DetailResponse> getJsonDetail(@Path("id") String id, @Query("api_key") String key);

    @GET("3/movie/{id}/videos?")
    Observable<TrailerResponse> getListTrailer(@Path("id") String id, @Query("api_key") String key);

    @GET("3/movie/{id}/reviews?")
    Observable<ReviewsResponse> getListReview(@Path("id") String id ,@Query("api_key") String key);

    @GET("3/search/movie?&language=en-US&page=1&include_adult=false&")
    Observable<PopulerResponse> getListSearch(@Query("api_key") String key, @Query("query") String query);

}
