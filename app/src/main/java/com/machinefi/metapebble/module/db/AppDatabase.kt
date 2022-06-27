package com.machinefi.metapebble.module.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.blankj.utilcode.util.Utils
import com.machinefi.metapebble.module.db.dao.ContractDao
import com.machinefi.metapebble.module.db.dao.DeviceDao
import com.machinefi.metapebble.module.db.dao.RecordDao
import com.machinefi.metapebble.module.db.entries.ContractEntry
import com.machinefi.metapebble.module.db.entries.DeviceEntry
import com.machinefi.metapebble.module.db.entries.RecordEntry

@Database(
    entities = [
        DeviceEntry::class,
        RecordEntry::class,
        ContractEntry::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun recordDao(): RecordDao
    abstract fun contractDao(): ContractDao

    companion object {

        val mInstance by lazy {
            Room.databaseBuilder(Utils.getApp(), AppDatabase::class.java, "pebble_db")
                .addMigrations(
                )
                .build()
        }
    }


}