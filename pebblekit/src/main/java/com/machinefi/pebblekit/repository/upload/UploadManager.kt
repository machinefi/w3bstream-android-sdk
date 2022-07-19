package com.machinefi.pebblekit.repository.upload

internal const val INTERVAL_SEND_DATA = 5

internal interface UploadManager {

    fun startUploading(hook: () -> String)

    fun stopUploading()

    fun uploadFrequency(frequency: Int)

    fun httpsServerUrl(url: String)

    fun socketServerUrl(url: String)
}