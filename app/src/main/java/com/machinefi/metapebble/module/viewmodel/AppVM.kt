package com.machinefi.metapebble.module.viewmodel

import androidx.lifecycle.viewModelScope
import com.machinefi.core.base.BaseViewModel
import com.machinefi.metapebble.module.repository.AppRepo
import kotlinx.coroutines.launch
import javax.inject.Inject

class AppVM @Inject constructor(val mAppRepo: AppRepo): BaseViewModel() {

    fun checkUpdate() {
        viewModelScope.launch {
            mAppRepo.queryVersion()
        }
    }

}