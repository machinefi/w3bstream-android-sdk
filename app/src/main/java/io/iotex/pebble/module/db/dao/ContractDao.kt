package io.iotex.pebble.module.db.dao

import androidx.room.*
import io.iotex.pebble.module.db.entries.ContractEntry
import io.iotex.pebble.module.db.entries.DeviceEntry
import io.iotex.pebble.module.db.entries.RecordEntry
import io.reactivex.Single

@Dao
interface ContractDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIfNonExist(contract: ContractEntry)

    @Query("select * from Contract")
    fun queryAll(): List<ContractEntry>

    @Query("select * from Contract where name = :name limit 1")
    fun queryContractByName(name: String): ContractEntry?

}