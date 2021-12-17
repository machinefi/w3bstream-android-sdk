package io.iotex.core.base.service

import android.os.Bundle
import android.view.View

interface IFragment {

    fun layoutResourceID(savedInstanceState: Bundle?): Int

    fun initView(view: View, savedInstanceState: Bundle?)

    fun initData(savedInstanceState: Bundle?)

    fun registerObserver()

}