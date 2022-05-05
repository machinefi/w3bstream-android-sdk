package io.iotex.pebble.constant

import io.iotex.pebble.module.db.entries.DeviceEntry

object PebbleStore {

    var mDevice: DeviceEntry? = null
        private set

    fun setDevice(device: DeviceEntry)  {
        mDevice = device
    }

}