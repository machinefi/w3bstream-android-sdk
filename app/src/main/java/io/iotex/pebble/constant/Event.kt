package io.iotex.pebble.constant

import io.iotex.pebble.module.db.entries.DeviceEntry

data class UpdateDeviceEvent(val device: DeviceEntry)

class QueryActivateResultEvent