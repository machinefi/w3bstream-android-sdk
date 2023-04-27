package com.machinefi.w3bstream.repository.network

data class Response<T>(
    val id: Long,
    val url: String,
    val result: T?
)