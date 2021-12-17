package io.iotex.core.base.service

import android.os.Bundle

interface IActivity {

    fun layoutResourceID(savedInstanceState: Bundle?): Int

    fun beforeInflate(savedInstanceState: Bundle?)

    fun initView(savedInstanceState: Bundle?)

    fun initData(savedInstanceState: Bundle?)

    fun registerObserver()
}