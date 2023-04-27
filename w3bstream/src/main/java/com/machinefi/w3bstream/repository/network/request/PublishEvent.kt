package com.machinefi.w3bstream.repository.network.request

import com.machinefi.w3bstream.utils.JsonUtil

data class PublishEvents(
    val events: List<Event>
): Payload {

    override fun toJson(): String {
        return JsonUtil.toJson(this)
    }
}

data class Event(
    val header: Header,
    val payload: String
)

data class Header(
    val event_id: String,
    val event_type: String,
    val pub_id: String,
    val pub_time: Long,
    val token: String
)