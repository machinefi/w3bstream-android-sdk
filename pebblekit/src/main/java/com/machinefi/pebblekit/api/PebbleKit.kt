package com.machinefi.pebblekit.api

import com.machinefi.pebblekit.repository.device.DeviceManager
import com.machinefi.pebblekit.repository.sign.SignManager
import com.machinefi.pebblekit.repository.upload.UploadManager

class PebbleKit private constructor(
    uploadManager: UploadManager,
    deviceManager: DeviceManager,
    signManager: SignManager,
) : UploadManager by uploadManager,
    DeviceManager by deviceManager,
    SignManager by signManager {

    init {

    }

    class Builder(config: PebbleKitConfig) {

        private val pebbleKitModule = PebbleKitModule(config)

        fun build() = PebbleKit(
            pebbleKitModule.uploadManager,
            pebbleKitModule.deviceManager,
            pebbleKitModule.signManager
        )
    }

}