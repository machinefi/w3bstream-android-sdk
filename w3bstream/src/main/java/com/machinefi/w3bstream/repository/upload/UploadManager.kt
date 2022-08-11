package com.machinefi.w3bstream.repository.upload

internal interface UploadManager {

    fun upload(data: String)

    fun updateServerApis(apis: List<String>)
}