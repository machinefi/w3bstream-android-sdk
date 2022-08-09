package com.machinefi.w3bstream

import com.machinefi.w3bstream.repository.auth.AuthManager
import com.machinefi.w3bstream.repository.upload.UploadManager
import com.machinefi.w3bstream.utils.KeystoreUtil
import io.reactivex.plugins.RxJavaPlugins

class W3bStreamKit private constructor(
    uploadManager: UploadManager,
    authManager: AuthManager,
) : UploadManager by uploadManager,
    AuthManager by authManager {

    init {
        RxJavaPlugins.setErrorHandler {
            it.printStackTrace()
        }

        KeystoreUtil.initPk()
    }

    class Builder(config: W3bStreamKitConfig) {

        private val w3bStreamKitModule = W3bStreamKitModule(config)

        fun build() = W3bStreamKit(
            w3bStreamKitModule.uploadManager,
            w3bStreamKitModule.authManager,
        )
    }

}