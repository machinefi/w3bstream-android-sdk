package com.machinefi.metapebble.module.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.machinefi.metapebble.module.db.entries.RecordEntry

@Dao
interface RecordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIfNonExist(record: RecordEntry)

    @Query("select * from Record where imei = :imei order by timestamp desc limit ((:page - 1)*:size), (:page*:size)")
    fun queryByImei(imei: String, page: Int, size: Int): List<RecordEntry>

}