package io.iotex.pebble.module.db.entries

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Device")
data class DeviceEntry(
    @PrimaryKey
    val imei: String,

    val sn: String,

    @ColumnInfo(name = "pub_key")
    val pubKey: String,

    var owner: String,

    var power: Int = DEVICE_POWER_OFF

) : Serializable

const val DEVICE_POWER_OFF = 0
const val DEVICE_POWER_ON = 1