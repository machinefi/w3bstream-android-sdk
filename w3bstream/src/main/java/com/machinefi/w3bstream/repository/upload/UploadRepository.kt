package com.machinefi.w3bstream.repository.upload

import com.machinefi.w3bstream.W3bStreamKitConfig
import com.machinefi.w3bstream.common.exception.IllegalServerException
import com.machinefi.w3bstream.common.request.ApiService
import com.machinefi.w3bstream.repository.auth.AuthManager
import com.machinefi.w3bstream.utils.extension.isValidServer

internal class UploadRepository(
    apiService: ApiService,
    private val config: W3bStreamKitConfig,
    private val authManager: AuthManager
): UploadManager {

    private val httpsUploader = HttpsUploader(apiService, config)
    private val webSocketUploader = WebSocketUploader(config)

    override fun upload(data: String) {
        val signature = authManager.sign(data.toByteArray())
        val pubKey = authManager.getPublicKey()
        httpsUploader.uploadData(data, signature, pubKey)
        webSocketUploader.uploadData(data, signature, pubKey)
    }

    @Throws(IllegalServerException::class)
    override fun updateServerApis(apis: List<String>) {
        val invalidServer = apis.firstOrNull {
            !it.isValidServer()
        }
        if (invalidServer != null) {
            throw IllegalServerException("This server $invalidServer is illegal")
        }
        config.serverApis.clear()
        config.serverApis.addAll(apis)
        webSocketUploader.resume()
    }
}