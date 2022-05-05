package io.iotex.pebble.module.db.dao

import androidx.room.*
import io.iotex.pebble.module.db.entries.DeviceEntry
import io.iotex.pebble.module.db.entries.RecordEntry
import io.reactivex.Single

@Dao
interface RecordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIfNonExist(record: RecordEntry)

    @Query("select * from Record where imei = :imei order by timestamp desc limit ((:page - 1)*:size), (:page*:size)")
    fun queryByImei(imei: String, page: Int, size: Int): List<RecordEntry>

}