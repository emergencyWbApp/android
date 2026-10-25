package `in`.emergencyWbApp.essentials.data.remote.dto



import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class PagesResponseDto(
    @SerializedName("pages") val pages: List<PageDto>
)

data class PageDto(
    @SerializedName("id")           val id: String,
    @SerializedName("title")        val title: String,
    @SerializedName("icon_outline") val iconOutline: String? = null,
    @SerializedName("icon_fill")    val iconFill: String? = null,
    @SerializedName("blocks")       val blocks: List<JsonElement>? = null
)