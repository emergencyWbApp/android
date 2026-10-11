package `in`.emergencyWbApp.essentials.data.local.entity




import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "app_config")
data class ConfigEntity(
    @PrimaryKey val key: String = "main",
    val appName: String,
    val tagline: String,
    val emergencyNumber: String,
    val contentVersion: String,
    val schemaVersion: String,
    val disclaimer: String,
    val essentialsEnabled: Boolean,
    val essentialsFree: Boolean,
    val premiumEnabled: Boolean,
    val premiumFree: Boolean,
    val syncedAt: Long = System.currentTimeMillis()
)