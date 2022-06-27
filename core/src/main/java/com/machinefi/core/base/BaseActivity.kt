package com.machinefi.core.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.machinefi.core.base.service.IActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

abstract class BaseActivity(private val layoutId: Int) : AppCompatActivity(), IActivity, HasAndroidInjector {

    @Inject
    lateinit var mVmFactory: ViewModelProvider.Factory

    @Inject
    lateinit var mAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        beforeInflate(savedInstanceState)
        super.onCreate(savedInstanceState)

        if (layoutId > 0) {
            setContentView(layoutId)
        }

        registerObserver()

        initView(savedInstanceState)

        initData(savedInstanceState)
    }

    override fun beforeInflate(savedInstanceState: Bundle?) {
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return mAndroidInjector
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }
}