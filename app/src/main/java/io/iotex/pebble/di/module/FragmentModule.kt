package io.iotex.pebble.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.iotex.pebble.pages.fragment.LoadingFragment

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun loadingFragment(): LoadingFragment

}