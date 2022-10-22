package com.machinefi.w3bstream.repository.upload

internal const val KEY_SERVER_APIS = "key_server_apis"

internal interface UploadManager {

    fun uploadData(data: String, publisherKey: String, publisherToken: String)

    fun addServerApi(api: String)

    fun removeServerApi(api: String)

    fun addServerApis(apis: List<String>)
}