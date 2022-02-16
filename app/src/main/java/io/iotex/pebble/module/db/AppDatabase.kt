package io.iotex.pebble.module.db

import android.database.sqlite.SQLiteException
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.blankj.utilcode.util.Utils
import io.iotex.pebble.module.db.dao.DeviceDao
import io.iotex.pebble.module.db.dao.RecordDao
import io.iotex.pebble.module.db.entries.DeviceEntry
import io.iotex.pebble.module.db.entries.RecordEntry
import io.iotex.pebble.utils.extension.e

@Database(
    entities = [
        DeviceEntry::class,
        RecordEntry::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun recordDao(): RecordDao

    companion object {

        val mInstance by lazy {
            Room.databaseBuilder(Utils.getApp(), AppDatabase::class.java, "pebble_db")
                .addMigrations(
                    MIGRATION_1_2
                )
                .build()
        }

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                try {
                    database.execSQL("CREATE TABLE IF NOT EXISTS Device(address TEXT NOT NULL, wallet_address TEXT NOT NULL, imei TEXT NOT NULL, sn TEXT NOT NULL, password TEXT NOT NULL, hash TEXT NOT NULL, status INTEGER NOT NULL, power INTEGER NOT NULL, PRIMARY KEY (imei))")
                } catch (e: SQLiteException) {
                    "MIGRATION_1_2 migrate ${e.message}".e()
                }
            }
        }

        private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                try {
                    database.execSQL("CREATE TABLE IF NOT EXISTS Record(imei TEXT NOT NULL, lng TEXT NOT NULL, lat TEXT NOT NULL, timestamp TEXT NOT NULL, PRIMARY KEY (timestamp))")
                } catch (e: SQLiteException) {
                    "MIGRATION_2_3 migrate ${e.message}".e()
                }
            }
        }
    }


}