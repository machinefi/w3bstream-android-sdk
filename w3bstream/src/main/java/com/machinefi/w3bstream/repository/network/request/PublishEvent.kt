package com.machinefi.w3bstream.repository.network.request

import com.machinefi.w3bstream.utils.JsonUtil

data class PublishEvent(
    val publisherKey: String,
    val publisherToken: String,
    val payload: String
): Payload {

    override fun toJson(): String {
        val time = System.currentTimeMillis() / 1000
        val header = Header("0x7FFFFFFF", publisherKey, time, publisherToken)
        val event = Event(header, payload)
        return JsonUtil.toJson(event)
    }
}

data class Event(
    val header: Header,
    val payload: String
)

data class Header(
    val event_type: String,
    val pub_id: String,
    val pub_time: Long,
    val token: String
)