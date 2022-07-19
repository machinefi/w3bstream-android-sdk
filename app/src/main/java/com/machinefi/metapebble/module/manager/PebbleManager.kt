package com.machinefi.metapebble.module.manager

import com.machinefi.metapebble.constant.HTTP_HOST
import com.machinefi.metapebble.constant.URL_HTTPS_SERVER
import com.machinefi.metapebble.constant.URL_SOCKET_SERVER
import com.machinefi.pebblekit.api.PebbleKit
import com.machinefi.pebblekit.api.PebbleKitConfig

object PebbleManager {

    private val config by lazy {
        PebbleKitConfig(
            HTTP_HOST,
            URL_HTTPS_SERVER,
            URL_SOCKET_SERVER
        )
    }

    val pebbleKit by lazy {
        PebbleKit.Builder(config).build()
    }

}