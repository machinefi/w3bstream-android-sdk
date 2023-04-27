package com.machinefi.w3bstream.repository.network

import com.fasterxml.jackson.core.type.TypeReference
import java.util.concurrent.atomic.AtomicLong

class Request<T>(
    val payload: String,
    val service: Service,
    val responseType: TypeReference<T>
) {
    val id: Long = nextId.getAndIncrement()

    fun send(): Response<T> {
        return service.send(this, responseType)
    }

    companion object {
        private val nextId = AtomicLong(0)
    }

}