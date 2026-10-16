package `in`.emergencyWbApp.essentials.ui

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import `in`.emergencyWbApp.essentials.data.remote.ApiConfig

/** Icon state — API-র state অনুযায়ী suffix ঠিক করে। */
enum class IconVariant(val suffix: String) {
    OUTLINE("-outline"),
    FILL("-fill")
}

/**
 * API-র icon name → full SVG URL।
 * - full URL দিলে (http...) সোজা ব্যবহার
 * - নাহলে: ICON_BASE_URL + name + suffix + ".svg"
 *
 * উদাহরণ:
 *   buildIconUrl("home", IconVariant.OUTLINE)
 *     → https://emergencywbapp.modernseva.in/icons/home-outline.svg
 *   buildIconUrl("home", IconVariant.FILL)
 *     → https://emergencywbapp.modernseva.in/icons/home-fill.svg
 */
fun buildIconUrl(name: String, variant: IconVariant): String {
    val trimmed = name.trim()
    if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) return trimmed

    // already has suffix হলে duplicate হবে না
    val withoutSuffix = trimmed
        .removeSuffix(".svg")
        .removeSuffix(".SVG")
        .removeSuffix("-outline")
        .removeSuffix("-fill")

    return "${ApiConfig.ICON_BASE_URL}${withoutSuffix}${variant.suffix}.svg"
}

/**
 * Dynamic SVG icon — API থেকে load, error হলে fixed Material warning।
 *
 * @param name    API-র icon name (extension ছাড়া) বা full URL
 * @param variant OUTLINE (unselected) বা FILL (selected)
 * @param tint    apply করার color; default = LocalContentColor (M3 auto)
 */
@Composable
fun DynamicIcon(
    name: String,
    variant: IconVariant,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    val url = remember(name, variant) { buildIconUrl(name, variant) }
    val painter = rememberAsyncImagePainter(model = url)
    val state by painter.state.collectAsState()

    when (state) {
        is AsyncImagePainter.State.Error -> {
            // ⚠️ SVG load fail হলে fixed Material warning icon
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = contentDescription,
                modifier = modifier,
                tint = tint
            )
        }
        else -> {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = ContentScale.Fit,
                colorFilter = if (tint != Color.Unspecified)
                    ColorFilter.tint(tint) else null
            )
        }
    }
}