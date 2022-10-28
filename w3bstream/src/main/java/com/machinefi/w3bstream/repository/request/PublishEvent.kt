package com.machinefi.w3bstream.repository.request

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.machinefi.w3bstream.utils.JsonUtil

data class PublishPayload(
    val publisherKey: String,
    val publisherToken: String,
    val payload: String
): Payload {

    override fun toJson(): String {
        val time = System.currentTimeMillis() / 1000
        val header = Header(0x7FFFFFFF, publisherKey, time, publisherToken)
        val event = PublishEvent(header, payload)
        return JsonUtil.toJson(event)
    }
}

data class PublishEvent(
    val header: Header,
    val payload: String
)

data class Header(
    val event_type: Long,
    val pub_id: String,
    val pub_time: Long,
    val token: String
)