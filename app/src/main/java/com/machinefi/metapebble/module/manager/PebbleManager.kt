package com.machinefi.metapebble.module.manager

import com.machinefi.metapebble.constant.HTTP_HOST
import com.machinefi.metapebble.constant.URL_UPLOAD_DATA
import com.machinefi.pebblekit.api.PebbleKit
import com.machinefi.pebblekit.api.PebbleKitConfig

object PebbleManager {

    private val config by lazy {
        PebbleKitConfig(
            HTTP_HOST,
            URL_UPLOAD_DATA
        )
    }

    val pebbleKit by lazy {
        PebbleKit.Builder(config).build()
    }



}