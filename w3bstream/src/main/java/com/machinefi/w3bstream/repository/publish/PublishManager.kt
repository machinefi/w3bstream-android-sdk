package com.machinefi.w3bstream.repository.publish

import com.machinefi.w3bstream.repository.network.Response
import com.machinefi.w3bstream.repository.network.response.PublishResult

interface PublishManager {

    fun publishEvent(url: String, publisherKey: String, publisherToken: String, payload: String): Response<Array<PublishResult>>?

}