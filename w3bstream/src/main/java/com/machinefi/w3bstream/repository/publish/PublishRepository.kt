package com.machinefi.w3bstream.repository.publish

import com.fasterxml.jackson.core.type.TypeReference
import com.machinefi.w3bstream.repository.network.Request
import com.machinefi.w3bstream.repository.network.Response
import com.machinefi.w3bstream.repository.network.Service
import com.machinefi.w3bstream.repository.network.request.PublishEvent
import com.machinefi.w3bstream.repository.network.response.PublishResult

internal class PublishRepository(
    private val service: Service
) : PublishManager {

    override fun publishEvent(
        url: String,
        publisherKey: String,
        publisherToken: String,
        payload: String
    ): Response<Array<PublishResult>>? {
        val event = PublishEvent(publisherKey, publisherToken, payload).toJson()
        val request =
            Request(url, event, service, object : TypeReference<Response<Array<PublishResult>>>() {})
        return request.send()
    }
}