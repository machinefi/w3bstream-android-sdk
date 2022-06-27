package com.machinefi.metapebble.di.module

import com.machinefi.metapebble.pages.fragment.LoadingFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun loadingFragment(): LoadingFragment

}