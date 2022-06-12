package io.iotex.pebble.module.http

data class SignPebbleBody(
    val imei: String,
    val sn: String,
    val pubkey: String
)

data class UploadMetadataBody(
    val imei: String,
    val payload: String,
)

