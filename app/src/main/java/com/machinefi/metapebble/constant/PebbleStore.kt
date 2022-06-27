package com.machinefi.metapebble.constant

import com.machinefi.metapebble.module.db.entries.DeviceEntry

object PebbleStore {

    var mDevice: DeviceEntry? = null
        private set

    fun setDevice(device: DeviceEntry)  {
        mDevice = device
    }

}