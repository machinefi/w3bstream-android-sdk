package com.machinefi.metapebble.module.db.entries

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Contract")
data class ContractEntry(

    @PrimaryKey
    val address: String,

    val name: String,

    val abi: String,

) : Serializable