package `in`.emergencyWbApp.essentials.ui





import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.emergencyWbApp.essentials.R

// ═════════════════════════════════════════════════════
// COLORS
// ═════════════════════════════════════════════════════
private val EmergencyRed = Color(0xFFD32F2F)
private val EmergencyRedDark = Color(0xFFB71C1C)
private val EmergencyRedLight = Color(0xFFEF5350)

private val PoliceBlue = Color(0xFF1565C0)
private val PoliceBlueDark = Color(0xFF0D47A1)

private val FireOrange = Color(0xFFE64A19)
private val FireOrangeDark = Color(0xFFBF360C)

private val AmbulanceRed = Color(0xFFC62828)
private val BloodRed = Color(0xFFB71C1C)

private val GreenSuccess = Color(0xFF2E7D32)
private val GreenLight = Color(0xFFE8F5E9)

private val CardBg = Color(0xFFFFFFFF)
private val ScreenBg = Color(0xFFF5F5F5)
private val TextPrimary = Color(0xFF212121)
private val TextSecondary = Color(0xFF757575)

// ═════════════════════════════════════════════════════
// MAIN HOME SCREEN
// ═════════════════════════════════════════════════════
@Composable
fun HomeScreen(
    onEmergencyCall: () -> Unit = {},
    onDistrictClick: () -> Unit = {},
    onQuickActionClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── 1. EMERGENCY 112 BUTTON ─────────────────
        EmergencyDialButton(onClick = onEmergencyCall)

        // ── 2. DISTRICT CARD ────────────────────────
        DistrictCard(
            district = stringResource(R.string.district_name),
            subtitle = stringResource(R.string.district_change),
            onClick = onDistrictClick
        )

        // ── 3. QUICK ACTION TITLE ───────────────────
        Text(
            text = stringResource(R.string.quick_actions),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            modifier = Modifier.padding(start = 4.dp)
        )

        // ── 4. QUICK ACTION GRID (2x2) ──────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.police),
                subtitle = stringResource(R.string.police_subtitle),
                icon = Icons.Filled.Shield,
                gradient = listOf(PoliceBlue, PoliceBlueDark),
                onClick = { onQuickActionClick("police") }
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.fire),
                subtitle = stringResource(R.string.fire_subtitle),
                icon = Icons.Filled.LocalFireDepartment,
                gradient = listOf(FireOrange, FireOrangeDark),
                onClick = { onQuickActionClick("fire") }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.ambulance),
                subtitle = stringResource(R.string.ambulance_subtitle),
                icon = Icons.Filled.LocalHospital,
                gradient = listOf(AmbulanceRed, EmergencyRedDark),
                onClick = { onQuickActionClick("ambulance") }
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.blood_bank),
                subtitle = stringResource(R.string.blood_bank_subtitle),
                icon = Icons.Filled.Bloodtype,
                gradient = listOf(BloodRed, EmergencyRedDark),
                onClick = { onQuickActionClick("blood") }
            )
        }

        // ── 5. OFFLINE INFO BANNER ──────────────────
        OfflineBanner()

        Spacer(Modifier.height(4.dp))
    }
}

// ═════════════════════════════════════════════════════
// 1. EMERGENCY 112 DIAL BUTTON (COMPACT)
// ═════════════════════════════════════════════════════
@Composable
private fun EmergencyDialButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow ring
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            EmergencyRed.copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Main button
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            EmergencyRedLight,
                            EmergencyRed,
                            EmergencyRedDark
                        )
                    )
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.emergency_number),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    lineHeight = 48.sp
                )
                Text(
                    text = stringResource(R.string.emergency_call),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.emergency_call_subtitle),
                    fontSize = 9.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ═════════════════════════════════════════════════════
// 2. DISTRICT CARD (COMPACT)
// ═════════════════════════════════════════════════════
@Composable
private fun DistrictCard(
    district: String,
    subtitle: String,
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
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(EmergencyRed)
            )

            Spacer(Modifier.width(10.dp))

            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint = EmergencyRed,
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = district,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ═════════════════════════════════════════════════════
// 3. QUICK ACTION CARD (COMPACT)
// ═════════════════════════════════════════════════════
@Composable
private fun QuickActionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(85.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradient))
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )

                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = subtitle,
                            fontSize = 9.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

// ═════════════════════════════════════════════════════
// 4. OFFLINE BANNER (COMPACT)
// ═════════════════════════════════════════════════════
@Composable
private fun OfflineBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = GreenLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.WifiOff,
                contentDescription = null,
                tint = GreenSuccess,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.offline_info),
                fontSize = 12.sp,
                color = GreenSuccess,
                fontWeight = FontWeight.Medium
            )
        }
    }
}