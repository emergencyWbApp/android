package `in`.emergencyWbApp.essentials.data.local.entity



import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pages")
data class PageEntity(
    @PrimaryKey val id: String,
    val title: String,
    val iconOutline: String,
    val iconFill: String,
    val order: Int,
    val module: String
)