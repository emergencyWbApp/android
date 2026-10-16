package `in`.emergencyWbApp.essentials.data.repository




import `in`.emergencyWbApp.essentials.data.local.dao.ConfigDao
import `in`.emergencyWbApp.essentials.data.local.dao.PageDao
import `in`.emergencyWbApp.essentials.data.local.entity.ConfigEntity
import `in`.emergencyWbApp.essentials.data.local.entity.PageEntity
import `in`.emergencyWbApp.essentials.data.remote.EmergencyApi
import `in`.emergencyWbApp.essentials.data.remote.dto.ConfigDto
import `in`.emergencyWbApp.essentials.data.remote.dto.PageDto
import kotlinx.coroutines.flow.Flow

class ConfigRepository(
    private val api: EmergencyApi,
    private val configDao: ConfigDao,
    private val pageDao: PageDao
) {

    fun getConfigFlow(): Flow<ConfigEntity?> = configDao.getConfigFlow()
    fun getPagesFlow(): Flow<List<PageEntity>> = pageDao.getPagesFlow()

    suspend fun hasCachedData(): Boolean =
        configDao.getConfig() != null && pageDao.getPages().isNotEmpty()

    /** API → Room. Fail হলে cache অটুট থাকে। */
    suspend fun syncAll(): Result<Unit> = try {
        val configDto = api.getConfig()
        val pagesDto  = api.getPages()

        // config — single row, REPLACE যথেষ্ট
        configDao.save(configDto.toEntity())

        // pages — orphan delete সহ
        val newPages = pagesDto.pages.map { it.toEntity() }
        if (newPages.isEmpty()) {
            pageDao.clear()
        } else {
            pageDao.saveAll(newPages)
            pageDao.deleteNotIn(newPages.map { it.id })
        }

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

private fun ConfigDto.toEntity() = ConfigEntity(
    key               = "main",
    appName           = appName,
    tagline           = tagline,
    emergencyNumber   = emergencyNumber,
    contentVersion    = contentVersion,
    schemaVersion     = schemaVersion,
    disclaimer        = disclaimer,
    essentialsEnabled = modules["essentials"]?.enabled ?: true,
    essentialsFree    = modules["essentials"]?.free    ?: true,
    premiumEnabled    = modules["premium"]?.enabled    ?: false,
    premiumFree       = modules["premium"]?.free       ?: false,
    themeJson         = `in`.emergencyWbApp.essentials.ui.theme.ThemeMapper.serialize(theme),
    syncedAt          = System.currentTimeMillis()
)

private fun PageDto.toEntity() = PageEntity(
    id     = id,
    title  = title,
    iconOutline   = iconOutline,
    iconFill = iconFill,
    order  = order,
    module = module
)