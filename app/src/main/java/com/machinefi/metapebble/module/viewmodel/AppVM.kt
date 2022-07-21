package com.machinefi.metapebble.module.viewmodel

import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.machinefi.core.base.BaseViewModel
import com.machinefi.metapebble.module.repository.AppRepo
import com.machinefi.metapebble.widget.UpdateDialog
import kotlinx.coroutines.launch
import javax.inject.Inject

class AppVM @Inject constructor(val mAppRepo: AppRepo): BaseViewModel() {

    fun checkUpdate() {
        viewModelScope.launch {
            val version = mAppRepo.queryVersion()
            if (version?.forced_code != null && version.forced_code >= AppUtils.getAppVersionCode()) {
                showUpdate(version.content ?: "", version.url, true)
                return@launch
            }
            if (version?.code != null && version.code > AppUtils.getAppVersionCode()) {
                showUpdate(version.content ?: "", version.url, false)
            }
        }
    }

    private fun showUpdate(content: String, url: String, force: Boolean) {
        val context = ActivityUtils.getTopActivity() ?: return
        UpdateDialog(context)
            .setContent(content)
            .cancellableOnTouchOutside(!force)
            .setUrl(url)
            .show()
    }

}