package `in`.emergencyWbApp.essentials.ui.theme


import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.google.gson.Gson
import `in`.emergencyWbApp.essentials.data.remote.dto.ThemeDto

object ThemeMapper {

    private val gson = Gson()

    /** ConfigEntity-র themeJson → ThemeDto */
    fun parseThemeDto(themeJson: String?): ThemeDto? = try {
        if (themeJson.isNullOrBlank()) null
        else gson.fromJson(themeJson, ThemeDto::class.java)
    } catch (e: Exception) { null }

    /** Any DTO → JSON (store করার জন্য) */
    fun serialize(dto: ThemeDto?): String? = try {
        if (dto == null) null else gson.toJson(dto)
    } catch (e: Exception) { null }

    /**
     * 3-layer merge:
     *   Layer 1: ThemeDefaults (hardcoded)
     *   Layer 2: API theme (nullable override)
     *   Layer 3: ThemeLocks (enforced)
     */
    fun buildTokens(theme: ThemeDto?): ThemeTokens {
        fun safeColor(hex: String?, fallback: Color): Color = try {
            if (hex.isNullOrBlank()) fallback else Color(hex.toColorInt())
        } catch (e: Exception) { fallback }

        fun clampInt(v: Int?, min: Int, max: Int, fallback: Int): Int =
            (v ?: fallback).coerceIn(min, max)

        return ThemeTokens(
            // 🆕 API primary — আর locked না
            primary          = safeColor(theme?.primary, ThemeDefaults.PRIMARY),
            // 🔒 Emergency red — API override করতে পারবে না
            emergencyRed     = ThemeLocks.EMERGENCY_RED,

            secondary        = safeColor(theme?.secondary,    ThemeDefaults.SECONDARY),
            background       = safeColor(theme?.background,   ThemeDefaults.BACKGROUND),
            surface          = safeColor(theme?.surface,      ThemeDefaults.SURFACE),
            onSurface        = safeColor(theme?.onSurface,    ThemeDefaults.ON_SURFACE),
            onBackground     = safeColor(theme?.onBackground, ThemeDefaults.ON_BACKGROUND),

            topBarBg         = safeColor(theme?.topBarBg,     ThemeDefaults.TOP_BAR_BG),
            topBarTitle      = safeColor(theme?.topBarTitle,  ThemeDefaults.TOP_BAR_TITLE),
            topBarTitleSizeSp = clampInt(
                theme?.topBarTitleSizeSp,
                ThemeLocks.MIN_TEXT_SP, 28,
                ThemeDefaults.TOP_BAR_TITLE_SP
            ),

            navBg            = safeColor(theme?.navBg,        ThemeDefaults.NAV_BG),
            // 🆕 API nav_selected — আর locked না
            navSelected      = safeColor(theme?.navSelected,  ThemeDefaults.NAV_SELECTED),
            navUnselected    = safeColor(theme?.navUnselected, ThemeDefaults.NAV_UNSELECTED),
            navIndicator     = safeColor(theme?.navIndicator, ThemeDefaults.NAV_INDICATOR),
            navIconSizeDp    = clampInt(
                theme?.navIconSizeDp,
                ThemeLocks.MIN_ICON_DP, ThemeLocks.MAX_ICON_DP,
                ThemeDefaults.NAV_ICON_DP
            ),
            navLabelSizeSp   = clampInt(
                theme?.navLabelSizeSp,
                ThemeLocks.MIN_TEXT_SP, 16,
                ThemeDefaults.NAV_LABEL_SP
            ),

            iconDefaultColor = safeColor(theme?.iconDefaultColor, ThemeDefaults.ICON_COLOR),
            iconDefaultSizeDp = clampInt(
                theme?.iconDefaultSizeDp,
                ThemeLocks.MIN_ICON_DP, ThemeLocks.MAX_ICON_DP,
                ThemeDefaults.ICON_SIZE_DP
            ),
            bodySizeSp       = clampInt(
                theme?.bodySizeSp,
                ThemeLocks.MIN_TEXT_SP, 20,
                ThemeDefaults.BODY_SP
            )
        )
    }
}