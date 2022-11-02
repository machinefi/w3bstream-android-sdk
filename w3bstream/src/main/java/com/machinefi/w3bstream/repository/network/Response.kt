package com.machinefi.w3bstream.repository.network

open class Response<T> {
    var id: Long = 0
    var url: String = ""
    val error: Error? = null
    val results: T? = null
}

data class Error(
    val code: Int,
    val message: String
)