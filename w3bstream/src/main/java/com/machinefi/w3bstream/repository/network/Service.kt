package com.machinefi.w3bstream.repository.network

import com.fasterxml.jackson.core.type.TypeReference

interface Service {

    fun <T> send(request: Request<T>, responseType: TypeReference<Response<T>>): Response<T>?

}