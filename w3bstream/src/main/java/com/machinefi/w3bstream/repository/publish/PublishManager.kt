package com.machinefi.w3bstream.repository.publish

import com.machinefi.w3bstream.repository.network.Response
import com.machinefi.w3bstream.repository.network.request.Event
import com.machinefi.w3bstream.repository.network.response.PublishResult

interface PublishManager {

    fun publishEvents(
        events: List<Event>
    ): Response<Array<PublishResult>>

}