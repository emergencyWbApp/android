package `in`.emergencyWbApp.essentials.data.local.dao

import androidx.room.*
import `in`.emergencyWbApp.essentials.data.local.entity.PageEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface PageDao {

    @Query("SELECT * FROM pages ORDER BY `order` ASC")
    fun getPagesFlow(): Flow<List<PageEntity>>

    @Query("SELECT * FROM pages ORDER BY `order` ASC")
    suspend fun getPages(): List<PageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAll(pages: List<PageEntity>)

    /** নতুন API list-এ নেই এমন পুরনো pages delete করে। */
    @Query("DELETE FROM pages WHERE id NOT IN (:keepIds)")
    suspend fun deleteNotIn(keepIds: List<String>)

    @Query("DELETE FROM pages")
    suspend fun clear()
}