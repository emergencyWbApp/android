package `in`.emergencyWbApp.essentials.data.remote.dto



import com.google.gson.annotations.SerializedName

/**
 * API থেকে আসা theme object। সব field nullable — missing হলে app default use করবে।
 */
data class ThemeDto(
    @SerializedName("primary")                val primary: String? = null,
    @SerializedName("secondary")              val secondary: String? = null,
    @SerializedName("background")             val background: String? = null,
    @SerializedName("surface")                val surface: String? = null,
    @SerializedName("on_surface")             val onSurface: String? = null,
    @SerializedName("on_background")          val onBackground: String? = null,

    @SerializedName("top_bar_bg")             val topBarBg: String? = null,
    @SerializedName("top_bar_title")          val topBarTitle: String? = null,
    @SerializedName("top_bar_title_size_sp")  val topBarTitleSizeSp: Int? = null,

    @SerializedName("nav_bg")                 val navBg: String? = null,
    @SerializedName("nav_selected")           val navSelected: String? = null,
    @SerializedName("nav_unselected")         val navUnselected: String? = null,
    @SerializedName("nav_indicator")          val navIndicator: String? = null,
    @SerializedName("nav_icon_size_dp")       val navIconSizeDp: Int? = null,
    @SerializedName("nav_label_size_sp")      val navLabelSizeSp: Int? = null,

    @SerializedName("icon_default_color")     val iconDefaultColor: String? = null,
    @SerializedName("icon_default_size_dp")   val iconDefaultSizeDp: Int? = null,

    @SerializedName("body_size_sp")           val bodySizeSp: Int? = null
)