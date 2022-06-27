package com.machinefi.metapebble.module.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.machinefi.metapebble.module.db.entries.ContractEntry

@Dao
interface ContractDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIfNonExist(contract: ContractEntry)

    @Query("select * from Contract")
    fun queryAll(): List<ContractEntry>

    @Query("select * from Contract where name = :name limit 1")
    fun queryContractByName(name: String): ContractEntry?

}