package com.machinefi.metapebble.module.db.dao

import androidx.room.*
import com.machinefi.metapebble.module.db.entries.DeviceEntry

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