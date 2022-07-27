package com.machinefi.pebblekit.api

import com.machinefi.pebblekit.repository.device.DeviceManager
import com.machinefi.pebblekit.repository.sign.SignManager
import com.machinefi.pebblekit.repository.upload.UploadManager

class W3bstreamKit private constructor(
    uploadManager: UploadManager,
    deviceManager: DeviceManager,
    signManager: SignManager,
) : UploadManager by uploadManager,
    DeviceManager by deviceManager,
    SignManager by signManager {

    init {

    }

    class Builder(config: W3bstreamKitConfig) {

        private val w3bstreamKitModule = W3bstreamKitModule(config)

        fun build() = W3bstreamKit(
            w3bstreamKitModule.uploadManager,
            w3bstreamKitModule.deviceManager,
            w3bstreamKitModule.signManager
        )
    }

}