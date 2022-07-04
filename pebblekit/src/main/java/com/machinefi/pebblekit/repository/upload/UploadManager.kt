package com.machinefi.pebblekit.repository.upload

internal const val INTERVAL_SEND_DATA = 5

internal interface UploadManager {

    fun startUploading(w3bStreamServer: String, hook: () -> String)

    fun stopUploading()

    fun uploadFrequency(frequency: Int)
}