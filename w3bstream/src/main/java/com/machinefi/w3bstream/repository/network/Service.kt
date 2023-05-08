package com.machinefi.w3bstream.repository.network

import com.fasterxml.jackson.core.type.TypeReference

interface Service {

    fun send(request: Request): Response?

}