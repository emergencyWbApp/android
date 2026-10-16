package `in`.emergencyWbApp.essentials.ui.theme



import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = EmergencyRed,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = PoliceBlue,
    background = ScreenBg,
    surface = CardBg,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun EssentialsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}