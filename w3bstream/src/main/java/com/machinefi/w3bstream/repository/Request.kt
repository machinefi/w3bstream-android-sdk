package com.machinefi.w3bstream.repository

import com.fasterxml.jackson.core.type.TypeReference
import java.util.concurrent.atomic.AtomicLong

class Request<T>(
    val url: String,
    val payload: String,
    val service: Service,
    val responseType: TypeReference<Response<T>>
) {
    private val nextId = AtomicLong(0)
    val id: Long = nextId.getAndIncrement()

    fun send(): Response<T>? {
        return service.send(this, responseType)
    }

}