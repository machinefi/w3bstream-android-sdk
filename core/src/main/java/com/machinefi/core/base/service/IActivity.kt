package com.machinefi.core.base.service

import android.os.Bundle

interface IActivity {

    fun beforeInflate(savedInstanceState: Bundle?)

    fun initView(savedInstanceState: Bundle?)

    fun initData(savedInstanceState: Bundle?)

    fun registerObserver()
}