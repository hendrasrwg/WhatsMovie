package com.example.me.whatsmovie.dagger;

import android.app.Application;

/**
 * Created by Me on 6/19/2017.
 */

public class MainApplication extends Application {

    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationComponent = DaggerApplicationComponent.builder()
                .appModule(new AppModule(this))
                .apiModule(new ApiModule("https://api.themoviedb.org/"))
                .build();
    }

    public ApplicationComponent getmApplicationComponent(){
        return mApplicationComponent;
    }

}
