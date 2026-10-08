package `in`.emergencyWbApp.essentials.ui



import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import `in`.emergencyWbApp.essentials.Language
import `in`.emergencyWbApp.essentials.LocaleHelper
import `in`.emergencyWbApp.essentials.R

private val EmergencyRed = Color(0xFFD32F2F)
private val TextPrimary = Color(0xFF212121)
private val TextSecondary = Color(0xFF757575)
private val CardBg = Color(0xFFFFFFFF)
private val ScreenBg = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var showLanguagePicker by remember { mutableStateOf(false) }

    // Current selected language
    var currentLang by remember {
        mutableStateOf(LocaleHelper.getSavedLanguage(context))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = ScreenBg
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // ── Top Bar ────────────────────────
                Surface(
                    color = Color.White,
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }

                        Text(
                            text = stringResource(R.string.settings_title),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    }
                }

                // ── Settings List ──────────────────
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ── Section: General ───────────
                    Text(
                        text = stringResource(R.string.settings_general),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    // Language Option
                    SettingsItem(
                        icon = Icons.Filled.Language,
                        title = stringResource(R.string.settings_language),
                        subtitle = getLanguageDisplayName(currentLang),
                        onClick = { showLanguagePicker = true }
                    )

                    Spacer(Modifier.height(8.dp))

                    // ── Section: About ─────────────
                    Text(
                        text = stringResource(R.string.settings_about),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    SettingsItem(
                        title = stringResource(R.string.settings_version),
                        subtitle = "1.0.0",
                        onClick = { }
                    )
                }
            }
        }
    }

    // ── Language Picker Dialog ──────────────────
    if (showLanguagePicker) {
        LanguagePickerDialog(
            currentLanguage = currentLang,
            onDismiss = { showLanguagePicker = false },
            onLanguageSelected = { code ->
                LocaleHelper.setLanguage(context, code)
                currentLang = code
                showLanguagePicker = false
                onDismiss()    // Settings dialog close — activity recreate hobe
            }
        )
    }
}

// ═════════════════════════════════════════════════════
// SETTINGS ITEM
// ═════════════════════════════════════════════════════
@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = EmergencyRed,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(14.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

// ═════════════════════════════════════════════════════
// LANGUAGE PICKER DIALOG
// ═════════════════════════════════════════════════════
@Composable
private fun LanguagePickerDialog(
    currentLanguage: String,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.settings_select_language),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LocaleHelper.supportedLanguages.forEach { language ->
                    LanguageItem(
                        language = language,
                        isSelected = language.code == currentLanguage,
                        onClick = { onLanguageSelected(language.code) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun LanguageItem(
    language: Language,
                         isSelected: Boolean,
                         onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) EmergencyRed.copy(alpha = 0.08f)
                else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = language.nativeName,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                color = if (isSelected) EmergencyRed else TextPrimary
            )
            Text(
                text = language.englishName,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Selected",
                tint = EmergencyRed,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ═════════════════════════════════════════════════════
// HELPER
// ═════════════════════════════════════════════════════
private fun getLanguageDisplayName(code: String): String {
    return LocaleHelper.supportedLanguages
        .firstOrNull { it.code == code }
        ?.nativeName ?: "English"
}