package io.iotex.pebble.module.viewmodel

import androidx.lifecycle.viewModelScope
import io.iotex.core.base.BaseViewModel
import io.iotex.pebble.constant.*
import io.iotex.pebble.module.repository.AppRepo
import kotlinx.coroutines.launch
import javax.inject.Inject

class AppVM @Inject constructor(val mAppRepo: AppRepo): BaseViewModel() {

    fun checkUpdate() {
        viewModelScope.launch {
            mAppRepo.queryVersion()
        }
    }

}