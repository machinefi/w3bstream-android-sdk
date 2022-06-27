package com.machinefi.metapebble.app

import android.content.Context
import androidx.multidex.MultiDex
import com.blankj.utilcode.util.AppUtils
import com.machinefi.core.base.BaseApplication
import com.machinefi.metapebble.di.component.DaggerAppComponent
import com.machinefi.metapebble.module.repository.AppRepo
import io.reactivex.plugins.RxJavaPlugins
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class PebbleApp : BaseApplication() {

    @Inject
    lateinit var mAppRepo: AppRepo

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent.builder()
                .build()
                .inject(this)

        RxJavaPlugins.setErrorHandler { t: Throwable? ->
            Timber.e(t)
        }

        this.registerActivityLifecycleCallbacks(ActivityLifecycleCallback())

        if (AppUtils.isAppDebug()) {
            Timber.plant(Timber.DebugTree())
        }

        GlobalScope.launch {
            mAppRepo.queryContractsFromRemote()
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}