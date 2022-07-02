package com.machinefi.pebblekit.repository.upload

internal interface UploadManager {

    suspend fun startUploading(hook: () -> String)

    suspend fun stopUploading()

}