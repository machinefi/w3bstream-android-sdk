package com.machinefi.w3bstream.repository.upload

import com.fasterxml.jackson.core.type.TypeReference
import com.machinefi.w3bstream.repository.Request
import com.machinefi.w3bstream.repository.Response
import com.machinefi.w3bstream.repository.Service
import com.machinefi.w3bstream.repository.request.PublishPayload
import com.machinefi.w3bstream.repository.response.UploadData

internal class UploadRepository(
    private val service: Service
) : UploadManager {

    override fun uploadData(
        url: String,
        data: String,
        publisherKey: String,
        publisherToken: String
    ): Response<UploadData>? {
        val payload = PublishPayload(publisherKey, publisherToken, data).toJson()
        val request = Request(url, payload, service, object : TypeReference<Response<UploadData>>() {})
        return request.send()
    }
}