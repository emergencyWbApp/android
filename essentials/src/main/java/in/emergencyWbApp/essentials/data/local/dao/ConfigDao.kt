package `in`.emergencyWbApp.essentials.data.local.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import `in`.emergencyWbApp.essentials.data.local.entity.ConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigDao {

    @Query("SELECT * FROM app_config WHERE `key` = 'main' LIMIT 1")
    fun getConfigFlow(): Flow<ConfigEntity?>

    @Query("SELECT * FROM app_config WHERE `key` = 'main' LIMIT 1")
    suspend fun getConfig(): ConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(config: ConfigEntity)

    @Query("DELETE FROM app_config")
    suspend fun clear()
}