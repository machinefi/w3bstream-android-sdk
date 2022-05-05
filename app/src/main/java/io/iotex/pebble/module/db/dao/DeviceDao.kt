package io.iotex.pebble.module.db.dao

import androidx.room.*
import io.iotex.pebble.module.db.entries.DeviceEntry
import io.reactivex.Single

@Dao
interface DeviceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIfNonExist(device: DeviceEntry)

    @Update
    fun update(device: DeviceEntry)

    @Query("select * from Device where imei = :imei limit 1")
    fun queryByImei(imei: String): DeviceEntry?

    @Query("select * from Device")
    fun queryAll(): List<DeviceEntry>

}