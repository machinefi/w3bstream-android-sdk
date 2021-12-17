package io.iotex.pebble.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.iotex.pebble.pages.CreatePebbleActivity
import io.iotex.pebble.pages.MainActivity
import io.iotex.pebble.pages.SplashActivity

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun mainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun splashActivity(): SplashActivity

    @ContributesAndroidInjector
    abstract fun createPebbleActivity(): CreatePebbleActivity

}