package com.machinefi.pebblekit.repository.upload

internal const val INTERVAL_SEND_DATA = 5000L

internal interface UploadManager {

    fun startUploading(hook: () -> String)

    fun stopUploading()

    fun uploadFrequency(mills: Long)

    fun httpsServerApi(api: String)

    fun socketServerApi(api: String)
}