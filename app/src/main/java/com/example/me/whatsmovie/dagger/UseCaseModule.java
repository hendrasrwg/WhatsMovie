package com.example.me.whatsmovie.dagger;

import com.example.me.whatsmovie.network.ApiInterface;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by Me on 6/18/2017.
 */

@Module
public class UseCaseModule {

    @Provides
    ApiInterface apiInterface(Retrofit retrofit){
        return retrofit.create(ApiInterface.class);
    }

}
