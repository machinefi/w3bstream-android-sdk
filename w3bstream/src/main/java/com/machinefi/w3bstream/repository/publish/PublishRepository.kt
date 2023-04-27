package com.machinefi.w3bstream.repository.publish

import com.fasterxml.jackson.core.type.TypeReference
import com.machinefi.w3bstream.repository.network.Request
import com.machinefi.w3bstream.repository.network.Response
import com.machinefi.w3bstream.repository.network.Service
import com.machinefi.w3bstream.repository.network.request.Event
import com.machinefi.w3bstream.repository.network.request.PublishEvents
import com.machinefi.w3bstream.repository.network.response.PublishResult

internal class PublishRepository(
    private val service: Service
) : PublishManager {

    override fun publishEvents(
        events: List<Event>
    ): Response<Array<PublishResult>> {
        val payload = PublishEvents(events).toJson()
        val request =
            Request("event", payload, service, object : TypeReference<Array<PublishResult>>() {})
        return request.send()
    }
}