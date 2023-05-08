package com.machinefi.w3bstream.repository.publish

import com.machinefi.w3bstream.repository.network.Request
import com.machinefi.w3bstream.repository.network.Response
import com.machinefi.w3bstream.repository.network.Service

internal class PublishRepository(
    private val service: Service
) : PublishManager {

    override fun publishEvents(type: String, payload: String): Response? {
        val request = Request(type, payload, service)
        return request.send()
    }
}