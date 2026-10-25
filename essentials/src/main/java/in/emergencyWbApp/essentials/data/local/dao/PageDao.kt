package `in`.emergencyWbApp.essentials.data.local.dao


import androidx.room.*
import `in`.emergencyWbApp.essentials.data.local.entity.PageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PageDao {

    // ✅ Bottom nav — API-র ক্রমে (rowid = insertion order)
    @Query("SELECT * FROM pages WHERE isMenu = 0 ORDER BY rowid ASC")
    fun getPagesFlow(): Flow<List<PageEntity>>

    @Query("SELECT * FROM pages WHERE isMenu = 0 ORDER BY rowid ASC")
    suspend fun getPages(): List<PageEntity>

    // ✅ 3-dots menu — API-র ক্রমে
    @Query("SELECT * FROM pages WHERE isMenu = 1 ORDER BY rowid ASC")
    fun getMenuPagesFlow(): Flow<List<PageEntity>>

    // 🧹 আগে সব delete করি, তারপর batch insert → rowid API ক্রম ধরে রাখবে
    @Query("DELETE FROM pages WHERE isMenu = 0")
    suspend fun clearMainPages()

    @Query("DELETE FROM pages WHERE isMenu = 1")
    suspend fun clearMenuPages()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAll(pages: List<PageEntity>)
}