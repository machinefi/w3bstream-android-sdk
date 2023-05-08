package com.machinefi.w3bstream.repository.network

class Request(
    val type: String,
    val payload: String,
    val service: Service
) {

    fun send(): Response? {
        return service.send(this)
    }

}