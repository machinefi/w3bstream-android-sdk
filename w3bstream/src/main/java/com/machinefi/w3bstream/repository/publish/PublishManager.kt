package com.machinefi.w3bstream.repository.publish

import com.machinefi.w3bstream.repository.network.Response

interface PublishManager {

    fun publishEvents(type: String, payload: String): Response?

}