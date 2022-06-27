package com.machinefi.core.base.service

import android.os.Bundle
import android.view.View

interface IFragment {

    fun initView(view: View, savedInstanceState: Bundle?)

    fun initData(savedInstanceState: Bundle?)

    fun registerObserver()

}