package com.machinefi.w3bstream

import com.machinefi.w3bstream.repository.network.Service
import com.machinefi.w3bstream.repository.publish.PublishManager
import com.machinefi.w3bstream.repository.publish.PublishRepository

class W3bStream(
    publishManager: PublishManager
) : PublishManager by publishManager {

    companion object {
        fun build(service: Service) = W3bStream(
            PublishRepository(service)
        )
    }

}