package io.iotex.pebble.di.module

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import io.iotex.pebble.di.scope.AppScope

@Module
class AppModule {

    @AppScope
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }
}