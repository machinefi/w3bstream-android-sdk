package io.iotex.pebble.module.http

data class SignPebbleBody(
    val imei: String,
    val sn: String,
    val pubkey: String
)


