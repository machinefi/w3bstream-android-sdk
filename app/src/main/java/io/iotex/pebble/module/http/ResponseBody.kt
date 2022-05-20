package io.iotex.pebble.module.http

data class SignPebbleResp(
    val imei: String,
    val sn: String,
    val timestamp: Long,
    val hash: String,
    val authentication: String
)
