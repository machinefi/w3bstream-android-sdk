package com.machinefi.w3bstream.repository.upload

internal interface UploadService {

    fun uploadData(data: String, publisherKey: String, publisherToken: String)

}