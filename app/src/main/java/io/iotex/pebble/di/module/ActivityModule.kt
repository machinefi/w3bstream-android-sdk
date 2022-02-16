package io.iotex.pebble.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.iotex.pebble.pages.*

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun splashActivity(): SplashActivity

    @ContributesAndroidInjector
    abstract fun createActivity(): CreateActivity

    @ContributesAndroidInjector
    abstract fun devicePanelActivity(): DevicePanelActivity

    @ContributesAndroidInjector
    abstract fun aboutActivity(): AboutActivity

    @ContributesAndroidInjector
    abstract fun historyActivity(): HistoryActivity

    @ContributesAndroidInjector
    abstract fun settingActivity(): SettingActivity

}