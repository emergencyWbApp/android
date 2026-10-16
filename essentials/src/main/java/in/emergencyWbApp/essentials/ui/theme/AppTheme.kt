package `in`.emergencyWbApp.essentials.ui.theme


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

/** ThemeTokens-কে composition-local-এ available করি যাতে UI যেকোনো জায়গায় পড়তে পারে। */
val LocalThemeTokens = staticCompositionLocalOf { ThemeDefaults.let {
    ThemeMapper.buildTokens(null)
} }

/**
 * পুরো app-এর root theme।
 * @param themeDto API থেকে আসা theme (null হলে default)
 */
@Composable
fun AppTheme(
    themeDto: `in`.emergencyWbApp.essentials.data.remote.dto.ThemeDto? = null,
    content: @Composable () -> Unit
) {
    val tokens = ThemeMapper.buildTokens(themeDto)

    val colorScheme = lightColorScheme(
        primary        = tokens.primary,
        onPrimary      = androidx.compose.ui.graphics.Color.White,
        secondary      = tokens.secondary,
        onSecondary    = androidx.compose.ui.graphics.Color.White,
        background     = tokens.background,
        onBackground   = tokens.onBackground,
        surface        = tokens.surface,
        onSurface      = tokens.onSurface,
//        error          = ThemeLocks.EMERGENCY_RED,
        onError        = androidx.compose.ui.graphics.Color.White
    )

    MaterialTheme(colorScheme = colorScheme) {
        CompositionLocalProvider(LocalThemeTokens provides tokens) {
            content()
        }
    }
}

/** Convenience accessor। */
object AppThemeValues {
    val tokens: ThemeTokens
        @Composable get() = LocalThemeTokens.current
}