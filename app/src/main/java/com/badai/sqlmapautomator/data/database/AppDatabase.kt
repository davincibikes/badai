package com.badai.sqlmapautomator.data.database

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.badai.sqlmapautomator.data.dao.ScanResultDao
import com.badai.sqlmapautomator.data.dao.TargetDao
import com.badai.sqlmapautomator.data.models.ScanResult
import com.badai.sqlmapautomator.data.models.Target
import java.util.Date

@Database(
    entities = [Target::class, ScanResult::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun targetDao(): TargetDao
    abstract fun scanResultDao(): ScanResultDao

    companion object {
        const val DATABASE_NAME = "badai_database"
    }
}

