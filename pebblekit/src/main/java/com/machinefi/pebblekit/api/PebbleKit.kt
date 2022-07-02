package com.machinefi.pebblekit.api

import com.machinefi.pebblekit.repository.upload.UploadManager

class PebbleKit private constructor(
    uploadManager: UploadManager
): UploadManager by uploadManager {

    class Builder(config: PebbleKitConfig) {

        private val pebbleKitModule = PebbleKitModule(config)

        fun build() = PebbleKit(pebbleKitModule.uploadRepository)
    }

}