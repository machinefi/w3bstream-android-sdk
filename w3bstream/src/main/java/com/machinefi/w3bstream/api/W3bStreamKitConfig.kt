package com.machinefi.w3bstream.api

class W3bStreamKitConfig(
    val signApi: String,
    val serverApis: List<String>
) {
    internal val innerServerApis = serverApis.toMutableList()
}