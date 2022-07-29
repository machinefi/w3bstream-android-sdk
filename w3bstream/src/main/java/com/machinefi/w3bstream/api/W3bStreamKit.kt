package com.machinefi.w3bstream.api

import com.machinefi.w3bstream.repository.device.DeviceManager
import com.machinefi.w3bstream.repository.sign.SignManager
import com.machinefi.w3bstream.repository.upload.UploadManager
import io.reactivex.plugins.RxJavaPlugins

class W3bStreamKit private constructor(
    uploadManager: UploadManager,
    deviceManager: DeviceManager,
    signManager: SignManager,
) : UploadManager by uploadManager,
    DeviceManager by deviceManager,
    SignManager by signManager {

    init {
        RxJavaPlugins.setErrorHandler {
            it.printStackTrace()
        }
    }

    class Builder(config: W3bStreamKitConfig) {

        private val w3bStreamKitModule = W3bStreamKitModule(config)

        fun build() = W3bStreamKit(
            w3bStreamKitModule.uploadManager,
            w3bStreamKitModule.deviceManager,
            w3bStreamKitModule.signManager
        )
    }

}