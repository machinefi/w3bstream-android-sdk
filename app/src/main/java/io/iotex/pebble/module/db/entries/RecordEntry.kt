package io.iotex.pebble.module.db.entries

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Record")
data class RecordEntry(

    val imei: String,

    val lng: String,

    val lat: String,

    @PrimaryKey
    val timestamp: String

) : Serializable