package `in`.emergencyWbApp.essentials.ui.theme


import androidx.compose.ui.graphics.Color

/**
 * Default theme — API fail হলে বা field missing হলে এগুলোই use হবে।
 * এগুলো কখনো null নয়, সবসময় valid।
 */
object ThemeDefaults {
    val PRIMARY          = Color(0xFFDC2626)
    val SECONDARY        = Color(0xFF2563EB)
    val BACKGROUND       = Color(0xFFFFFFFF)
    val SURFACE          = Color(0xFFF7F7F8)
    val ON_SURFACE       = Color(0xFF111827)
    val ON_BACKGROUND    = Color(0xFF111827)

    val TOP_BAR_BG       = Color(0xFFFFFFFF)
    val TOP_BAR_TITLE    = Color(0xFF111827)
    val TOP_BAR_TITLE_SP = 20

    val NAV_BG           = Color(0xFFFFFFFF)
    val NAV_SELECTED     = Color(0xFFDC2626)
    val NAV_UNSELECTED   = Color(0xFF6B7280)
    val NAV_INDICATOR    = Color(0xFFFEE2E2)
    val NAV_ICON_DP      = 24
    val NAV_LABEL_SP     = 11

    val ICON_COLOR       = Color(0xFF111827)
    val ICON_SIZE_DP     = 24

    val BODY_SP          = 14
}

/**
 * 🔒 Safety locks — API এই মানগুলো override করতে পারবে না।
 */
object ThemeLocks {
    val EMERGENCY_RED     = Color(0xFFDC2626)
    val MIN_TEXT_SP       = 11
    val MIN_TOUCH_DP      = 48
    val MIN_ICON_DP       = 16
    val MAX_ICON_DP       = 48
}

/**
 * Runtime theme tokens — defaults + API merge-এর final result।
 * সবসময় non-null।
 */
data class ThemeTokens(
    val primary: Color,
    val emergencyRed: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val onSurface: Color,
    val onBackground: Color,

    val topBarBg: Color,
    val topBarTitle: Color,
    val topBarTitleSizeSp: Int,

    val navBg: Color,
    val navSelected: Color,
    val navUnselected: Color,
    val navIndicator: Color,
    val navIconSizeDp: Int,
    val navLabelSizeSp: Int,

    val iconDefaultColor: Color,
    val iconDefaultSizeDp: Int,

    val bodySizeSp: Int
)