package `in`.emergencyWbApp.essentials


import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LocaleHelper {

    private const val PREF_NAME = "language_pref"
    private const val KEY_LANG = "selected_language"

    val supportedLanguages = listOf(
        Language("en", "English", "English"),
        Language("bn", "বাংলা", "Bangla"),
        Language("hi", "हिन्दी", "Hindi"),
    )

    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANG, "en") ?: "en"
    }

    fun setLanguage(context: Context, langCode: String) {
        // Save to prefs
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANG, langCode).apply()

        // Apply via AppCompatDelegate (Android 13+ e auto handle)
        val locale = LocaleListCompat.forLanguageTags(langCode)
        AppCompatDelegate.setApplicationLocales(locale)

        // Fallback: update configuration manually (older Android)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            updateConfiguration(context, langCode)
        }
    }

    private fun updateConfiguration(context: Context, langCode: String) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}

data class Language(
    val code: String,
    val nativeName: String,
    val englishName: String
)