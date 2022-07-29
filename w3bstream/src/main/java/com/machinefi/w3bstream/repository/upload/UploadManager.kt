package com.machinefi.w3bstream.repository.upload

internal const val INTERVAL_SEND_DATA = 300L

internal interface UploadManager {

    fun startUpload(hook: () -> String)

    fun stopUpload()

    fun setUploadInterval(seconds: Long)

    fun setHttpsServerApi(api: String)

    fun setWebSocketServerApi(api: String)
}