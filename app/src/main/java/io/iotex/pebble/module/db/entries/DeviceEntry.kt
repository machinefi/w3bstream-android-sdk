package io.iotex.pebble.module.db.entries

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Device")
data class DeviceEntry(

    val address: String,

    @PrimaryKey
    val imei: String,

    val sn: String,

    val password: String,

    val hash: String,

    @ColumnInfo(name = "pub_key")
    val pubKey: String,

    var owner: String,

    var status: Int = DEVICE_STATUS_UNREGISTER,

    var power: Int = DEVICE_POWER_OFF

) : Serializable

const val DEVICE_STATUS_UNREGISTER = 0
const val DEVICE_STATUS_PROPOSE = 1
const val DEVICE_STATUS_CONFIRM = 2

const val DEVICE_POWER_OFF = 0
const val DEVICE_POWER_ON = 1