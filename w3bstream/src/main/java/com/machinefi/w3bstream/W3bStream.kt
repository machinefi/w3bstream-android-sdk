package com.machinefi.w3bstream

import com.machinefi.w3bstream.repository.Service
import com.machinefi.w3bstream.repository.upload.UploadManager
import com.machinefi.w3bstream.repository.upload.UploadRepository

class W3bStream(
    uploadManager: UploadManager
) : UploadManager by uploadManager {

    companion object {
        fun build(service: Service) = W3bStream(
            UploadRepository(service)
        )
    }

}