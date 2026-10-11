package `in`.emergencyWbApp.essentials.data.local


import androidx.room.Database
import androidx.room.RoomDatabase

import `in`.emergencyWbApp.essentials.data.local.dao.ConfigDao
import `in`.emergencyWbApp.essentials.data.local.dao.PageDao
import `in`.emergencyWbApp.essentials.data.local.entity.ConfigEntity
import `in`.emergencyWbApp.essentials.data.local.entity.PageEntity

@Database(
    entities = [
        PageEntity::class,
        ConfigEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class EmergencyDatabase : RoomDatabase() {
    abstract fun pageDao(): PageDao
    abstract fun configDao(): ConfigDao
}