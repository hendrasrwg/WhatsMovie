package com.example.me.whatsmovie.dagger;

import com.example.me.whatsmovie.MainActivity;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;
import dagger.Subcomponent;

/**
 * Created by Me on 6/18/2017.
 */

@Singleton
@Component(modules = {ApiModule.class, UseCaseModule.class,AppModule.class})
public interface ApplicationComponent {

    void inject(MainActivity activity);

}