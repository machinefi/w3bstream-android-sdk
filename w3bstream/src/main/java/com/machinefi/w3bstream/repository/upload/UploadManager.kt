package com.machinefi.w3bstream.repository.upload

import com.machinefi.w3bstream.repository.Response
import com.machinefi.w3bstream.repository.response.UploadData

interface UploadManager {

    fun uploadData(url: String, data: String, publisherKey: String, publisherToken: String): Response<UploadData>?

}