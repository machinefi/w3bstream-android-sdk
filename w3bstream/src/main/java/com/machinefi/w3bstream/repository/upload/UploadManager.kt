package com.machinefi.w3bstream.repository.upload

internal const val INTERVAL_SEND_DATA = 300L
internal const val KEY_SERVER_APIS = "key_server_apis"

internal interface UploadManager {

    fun startUpload(hook: () -> String)

    fun stopUpload()

    fun setUploadInterval(seconds: Long)

    fun addServerApi(api: String)

    fun removeServerApi(api: String)

    fun addServerApis(apis: List<String>)
}