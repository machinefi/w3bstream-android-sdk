package com.machinefi.core.base

import android.app.Application
import com.machinefi.core.base.service.IApp
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
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