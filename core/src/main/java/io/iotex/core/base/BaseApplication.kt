package io.iotex.core.base

import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.iotex.core.base.service.IApp
import javax.inject.Inject


open class BaseApplication : Application(), IApp, HasAndroidInjector {

    @Inject
    lateinit var mDispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return mDispatchingAndroidInjector
    }

}