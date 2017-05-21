package com.example.me.whatsmovie.network;

import com.example.me.whatsmovie.model.PopulerResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by Me on 5/21/2017.
 */

public interface ApiInterface {

    @GET("3/movie/popular?api_key=e160bcf528f65e469ce610a7e7cc1c1b&language=en-US&page=1")
    Observable<PopulerResponse> getJsonPopuler();

}
