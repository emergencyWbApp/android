package `in`.emergencyWbApp.essentials

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import `in`.emergencyWbApp.essentials.data.local.EmergencyDatabase
import `in`.emergencyWbApp.essentials.data.local.entity.ConfigEntity
import `in`.emergencyWbApp.essentials.data.local.entity.PageEntity
import `in`.emergencyWbApp.essentials.data.remote.ApiConfig
import `in`.emergencyWbApp.essentials.data.remote.EmergencyApi
import `in`.emergencyWbApp.essentials.data.repository.ConfigRepository
import `in`.emergencyWbApp.essentials.ui.iconFor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EssentialsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    EssentialsRoot()
                }
            }
        }
    }
}

/** Manual DI — Hilt ছাড়া */
private fun createRepository(context: android.content.Context): ConfigRepository {
    val db = Room.databaseBuilder(
        context.applicationContext,
        EmergencyDatabase::class.java,
        "emergency_wb.db"
    ).fallbackToDestructiveMigration().build()

    val api = Retrofit.Builder()
        .baseUrl(ApiConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(EmergencyApi::class.java)

    return ConfigRepository(api, db.configDao(), db.pageDao())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EssentialsRoot() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { createRepository(context) }

    var config by remember { mutableStateOf<ConfigEntity?>(null) }
    var pages by remember { mutableStateOf<List<PageEntity>>(emptyList()) }
    var selectedPageId by remember { mutableStateOf<String?>(null) }
    var isInitialLoading by remember { mutableStateOf(true) }
    var isSyncing by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // ── Room flow: config ───────────────────────────────────
    LaunchedEffect(Unit) {
        repo.getConfigFlow().collectLatest { config = it }
    }

    // ── Room flow: pages ────────────────────────────────────
    LaunchedEffect(Unit) {
        repo.getPagesFlow().collectLatest { list ->
            pages = list
            if (selectedPageId == null || list.none { it.id == selectedPageId }) {
                selectedPageId = list.firstOrNull()?.id
            }
        }
    }

    // ── Shared sync routine ─────────────────────────────────
    suspend fun performSync() {
        isSyncing = true
        errorMsg = null
        val result = withContext(Dispatchers.IO) { repo.syncAll() }
        errorMsg = result.exceptionOrNull()?.message
        isSyncing = false
        isInitialLoading = false
    }

    LaunchedEffect(Unit) { performSync() }

    // ── States ──────────────────────────────────────────────
    val hasContent = config != null && pages.isNotEmpty()

    when {

        isInitialLoading && !hasContent -> {
            LoadingScreen(
                isSyncing = isSyncing,
                onReload = { scope.launch { performSync() } }
            )
        }

        !hasContent -> {
            OfflineFallbackScreen(
                isSyncing = isSyncing,
                errorMessage = errorMsg,
                onRetry = { scope.launch { performSync() } }
            )
        }

        else -> {
            val pagerState = rememberPagerState(pageCount = { pages.size })

            // 🔑 GUARD — pages কমে গেলে invalid index ঠিক করো
            //    (উদাহরণ: 4 pages ছিল, API-তে 3 হল → currentPage 3 was invalid)
            LaunchedEffect(pages.size, pagerState.currentPage) {
                if (pages.isNotEmpty() && pagerState.currentPage >= pages.size) {
                    pagerState.scrollToPage(pages.size - 1)
                }
            }

            // ── Nav tap → scroll pager ──────────────────────
            LaunchedEffect(selectedPageId, pages) {
                val idx = pages.indexOfFirst { it.id == selectedPageId }
                if (idx >= 0 && idx != pagerState.currentPage) {
                    pagerState.animateScrollToPage(idx)
                }
            }

            // ── Pager settle → selectedPageId (swipe) ───────
            LaunchedEffect(pagerState, pages) {
                snapshotFlow { pagerState.currentPage to pagerState.isScrollInProgress }
                    .collect { (pageIdx, scrolling) ->
                        if (!scrolling) {
                            pages.getOrNull(pageIdx)?.let { p ->
                                if (p.id != selectedPageId) {
                                    selectedPageId = p.id
                                }
                            }
                        }
                    }
            }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(config!!.appName) },
                        actions = {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(Modifier.width(16.dp))
                            } else {
                                IconButton(onClick = {
                                    scope.launch { performSync() }
                                }) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Sync")
                                }
                            }
                        }
                    )
                },
                bottomBar = {
                    NavigationBar {
                        pages.forEach { page ->
                            NavigationBarItem(
                                selected = selectedPageId == page.id,
                                onClick = {
                                    if (selectedPageId == page.id) {
                                        scope.launch {
                                            pagerState.animateScrollToPage(
                                                pages.indexOf(page)
                                            )
                                        }
                                    } else {
                                        selectedPageId = page.id
                                    }
                                },
                                icon = {
                                    Icon(iconFor(page.icon), contentDescription = page.title)
                                },
                                label = { Text(page.title, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            ) { padding ->
                HorizontalPager(
                    state = pagerState,
                    key = { pages[it].id },
                    userScrollEnabled = true,
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) { index ->
                    PagePlaceholder(page = pages[index])
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────
// Loading screen — English only
// ──────────────────────────────────────────────────────────

@Composable
private fun LoadingScreen(
    isSyncing: Boolean,
    onReload: () -> Unit
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            CircularProgressIndicator()
            Spacer(Modifier.height(16.dp))

            Text(
                "Loading…",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(28.dp))

            OutlinedButton(
                onClick = onReload,
                enabled = !isSyncing,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(48.dp)
            ) {
                if (isSyncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(10.dp))
                    Text("Retrying…", fontSize = 14.sp)
                } else {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Reload", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                "Taking too long? Check your internet and tap Reload.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ──────────────────────────────────────────────────────────
// Offline fallback — Bengali
// ──────────────────────────────────────────────────────────

@Composable
private fun OfflineFallbackScreen(
    isSyncing: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = "ইন্টারনেট সংযোগ নেই",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "No Internet Connection",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "প্রথমবার অ্যাপ চালু করতে ইন্টারনেট দরকার। " +
                    "একবার ডাউনলোড হয়ে গেলে, এরপর ইন্টারনেট ছাড়াই চলবে।",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = onRetry,
            enabled = !isSyncing,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            if (isSyncing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(10.dp))
                Text("সংযোগ করা হচ্ছে…", fontSize = 15.sp)
            } else {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("আবার চেষ্টা করুন", fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }
        }

        if (!isSyncing && errorMessage != null) {
            Spacer(Modifier.height(14.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFDC2626).copy(alpha = 0.10f)
            )
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "🚨 জরুরি অবস্থা?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "এখনই 112-এ কল করুন। অ্যাপ ছাড়াই সাহায্য পাবেন।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF7F1D1D)
                )
            }
        }
    }
}

// ──────────────────────────────────────────────────────────
// Page content placeholder
// ──────────────────────────────────────────────────────────

@Composable
private fun PagePlaceholder(page: PageEntity) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                iconFor(page.icon),
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))
            Text(
                page.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}