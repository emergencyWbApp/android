package `in`.emergencyWbApp.essentials

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log.i
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpOffset

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import `in`.emergencyWbApp.essentials.data.local.EmergencyDatabase
import `in`.emergencyWbApp.essentials.data.local.entity.ConfigEntity
import `in`.emergencyWbApp.essentials.data.local.entity.PageEntity
import `in`.emergencyWbApp.essentials.data.remote.ApiConfig

import `in`.emergencyWbApp.essentials.data.remote.EmergencyApi
import `in`.emergencyWbApp.essentials.data.repository.ConfigRepository

import `in`.emergencyWbApp.essentials.ui.theme.AppTheme
import `in`.emergencyWbApp.essentials.ui.theme.AppThemeValues
import `in`.emergencyWbApp.essentials.ui.theme.ThemeMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import `in`.emergencyWbApp.essentials.ui.DynamicIcon
import `in`.emergencyWbApp.essentials.ui.IconVariant
import `in`.emergencyWbApp.essentials.ui.QuickActionSheet
import `in`.emergencyWbApp.essentials.ui.blocks.BlockParser
import `in`.emergencyWbApp.essentials.ui.blocks.RenderBlocks
import `in`.emergencyWbApp.essentials.ui.theme.MeshGradientBackground
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EssentialsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔑 Startup log — কোন environment এ চলছে
        i("ENV_CHECK", "═══════════════════════════════════════")
        i("ENV_CHECK", "BASE_URL    = ${ApiConfig.BASE_URL}")
        i("ENV_CHECK", "ICON_URL    = ${ApiConfig.ICON_BASE_URL}")
        i("ENV_CHECK", "═══════════════════════════════════════")

        // 🔑 Coil 3 — SVG support
        SingletonImageLoader.setSafe { context ->
            ImageLoader.Builder(context)
                .components { add(SvgDecoder.Factory()) }
                .crossfade(true)
                .build()
        }

        setContent {
            // 🎯 AppTheme এখানে না — কারণ config এখনো load হয়নি।
            //    ভেতরে config load হওয়ার পরে wrap হবে।
            EssentialsRoot()
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

    // 🆕 Menu state
    var menuPages by remember { mutableStateOf<List<PageEntity>>(emptyList()) }
    var menuExpanded by remember { mutableStateOf(false) }

    // 🆕 Quick Action Bottom Sheet State
    var showQuickActionSheet by remember { mutableStateOf(false) }
    var selectedAction by remember { mutableStateOf<String?>(null) }
    var selectedTitle by remember { mutableStateOf("") }
    var selectedSubtitle by remember { mutableStateOf("") }

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

    // 🆕 Room flow: menu pages
    LaunchedEffect(Unit) {
        repo.getMenuPagesFlow().collectLatest { list ->
            menuPages = list
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

    // 🎯 AppTheme wrap — theme API থেকে, defaults + locks merge
    AppTheme(themeDto = ThemeMapper.parseThemeDto(config?.themeJson)) {

        MeshGradientBackground {

            when {

                // (1) Loading — প্রথমবার sync চলছে
                isInitialLoading && !hasContent -> {
                    LoadingScreen(
                        isSyncing = isSyncing,
                        onReload = { scope.launch { performSync() } }
                    )
                }

                // (2) No data — sync fail, cache খালি
                !hasContent -> {
                    OfflineFallbackScreen(
                        isSyncing = isSyncing,
                        errorMessage = errorMsg,
                        onRetry = { scope.launch { performSync() } }
                    )
                }

                // (3) Content ready
                else -> {
                    val pagerState = rememberPagerState(pageCount = { pages.size })

                    // 🔑 GUARD — pages কমে গেলে invalid index ঠিক করো
                    LaunchedEffect(pages.size, pagerState.currentPage) {
                        if (pages.isNotEmpty() && pagerState.currentPage >= pages.size) {
                            pagerState.scrollToPage(pages.size - 1)
                        }
                    }

                    // ── Nav tap → scroll pager ──────────────
                    LaunchedEffect(selectedPageId, pages) {
                        val idx = pages.indexOfFirst { it.id == selectedPageId }
                        if (idx >= 0 && idx != pagerState.currentPage) {
                            pagerState.animateScrollToPage(idx)
                        }
                    }

                    // ── Pager settle → selectedPageId (swipe) ───
                    LaunchedEffect(pagerState, pages) {
                        snapshotFlow {
                            pagerState.currentPage to pagerState.isScrollInProgress
                        }.collect { (pageIdx, scrolling) ->
                            if (!scrolling) {
                                pages.getOrNull(pageIdx)?.let { p ->
                                    if (p.id != selectedPageId) {
                                        selectedPageId = p.id
                                    }
                                }
                            }
                        }
                    }

                    // 🎨 Theme tokens — UI-তে apply করার জন্য
                    val tokens = AppThemeValues.tokens

                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        config!!.appName,
                                        fontSize = tokens.topBarTitleSizeSp.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Color.Transparent,
                                    titleContentColor = tokens.topBarTitle
                                ),
                                actions = {
                                    // 1️⃣ Sync / Refresh button
                                    if (isSyncing) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp,
                                            color = tokens.topBarTitle
                                        )
                                        Spacer(Modifier.width(16.dp))
                                    } else {
                                        IconButton(onClick = {
                                            scope.launch { performSync() }
                                        }) {
                                            Icon(
                                                Icons.Default.Refresh,
                                                contentDescription = "Sync",
                                                tint = tokens.topBarTitle
                                            )
                                        }
                                    }

                                    // 2️⃣ 🎯 3-Dots Menu
                                    Box {
                                        IconButton(onClick = { menuExpanded = true }) {
                                            Icon(
                                                Icons.Default.MoreVert,
                                                contentDescription = "More options",
                                                tint = tokens.topBarTitle
                                            )
                                        }

                                        DropdownMenu(
                                            expanded = menuExpanded,
                                            onDismissRequest = { menuExpanded = false },
                                            offset = DpOffset(0.dp, 8.dp)
                                        ) {
                                            if (menuPages.isEmpty()) {
                                                DropdownMenuItem(
                                                    text = {
                                                        Text(
                                                            "No menu items",
                                                            fontSize = 14.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    },
                                                    onClick = { menuExpanded = false }
                                                )
                                            } else {
                                                menuPages.forEach { page ->
                                                    DropdownMenuItem(
                                                        text = {
                                                            Text(
                                                                text = page.title,
                                                                fontSize = 15.sp,
                                                                fontWeight = FontWeight.Medium
                                                            )
                                                        },
                                                        onClick = {
                                                            menuExpanded = false
                                                            // TODO: page.id অনুযায়ী navigation বা action
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            )
                        },
                        bottomBar = {

                                NavigationBar(containerColor = tokens.navBg) {
                                    pages.forEach { page ->
                                        val isSelected = selectedPageId == page.id

                                        NavigationBarItem(
                                            selected = isSelected,
                                            onClick = {
                                                if (isSelected) {
                                                    scope.launch {
                                                        pagerState.animateScrollToPage(
                                                            pages.indexOf(
                                                                page
                                                            )
                                                        )
                                                    }
                                                } else {
                                                    selectedPageId = page.id
                                                }
                                            },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = tokens.navSelected,
                                                selectedTextColor = tokens.navSelected,
                                                indicatorColor = tokens.navIndicator,
                                                unselectedIconColor = tokens.navUnselected,
                                                unselectedTextColor = tokens.navUnselected
                                            ),
                                            icon = {
                                                // Null safety handled with ?: ""
                                                DynamicIcon(
                                                    name = if (isSelected) page.iconFill
                                                        ?: "" else page.iconOutline ?: "",
                                                    variant = if (isSelected) IconVariant.FILL else IconVariant.OUTLINE,
                                                    contentDescription = page.title,
                                                    modifier = Modifier.size(tokens.navIconSizeDp.dp)
                                                )
                                            },
                                            label = {
                                                Text(
                                                    page.title,
                                                    fontSize = tokens.navLabelSizeSp.sp
                                                )
                                            }
                                        )
                                    }
                                }

                        },
                        containerColor = Color.Transparent,
                        contentWindowInsets = WindowInsets(0, 0, 0, 0)
                    ) { padding ->
                        HorizontalPager(
                            state = pagerState,
                            key = { pages[it].id },
                            userScrollEnabled = true,
                            modifier = Modifier
                                .padding(padding)
                                .fillMaxSize()
                        ) { index ->
                            // 🎯 এখানে PageContent কল করা হয়েছে
                            PageContent(
                                page = pages[index],
                                context = context,
                                onActionClick = { action, title, subtitle ->
                                    // 🎯 Quick Action Popup-এর জন্য স্টেট সেট করা হচ্ছে
                                    selectedAction = action
                                    selectedTitle = title
                                    selectedSubtitle = subtitle
                                    showQuickActionSheet = true
                                })
                        }
                        // 🎯 Quick Action Bottom Sheet
                        if (showQuickActionSheet && selectedAction != null) {
                            QuickActionSheet(
                                title = selectedTitle,
                                subtitle = selectedSubtitle,
                                actionType = selectedAction!!,
                                onDismiss = { showQuickActionSheet = false },
                                onCallClick = {
                                    val number = selectedAction!!.removePrefix("tel:").trim()
                                    if (number.isNotEmpty() && number != "xxx") {
                                        try {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "No dialer app found", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                onViewDetailsClick = {
                                    Toast.makeText(context, "Navigate to: ${selectedAction!!.removePrefix("navigate:")}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                    }
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
                fontWeight = Medium
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
                    Text("Reload", fontSize = 14.sp, fontWeight = Medium)
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
                Text(
                    "আবার চেষ্টা করুন",
                    fontSize = 15.sp,
                    fontWeight = Medium
                )
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
// Page Content Renderer (Dynamic Blocks)
// ──────────────────────────────────────────────────────────

@Composable
private fun PageContent(
    page: PageEntity,
    context: Context,
    onActionClick: (action: String, title: String, subtitle: String) -> Unit
) {
    val blocks = remember(page.blocksJson) {
        BlockParser.parseJsonString(page.blocksJson)
    }

    if (blocks.isEmpty()) {
        // ব্লক খালি থাকলে
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                DynamicIcon(
                    name = page.iconOutline ?: "",
                    variant = IconVariant.OUTLINE,
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
                Spacer(Modifier.height(8.dp))
                Text("Coming Soon", color = Color.Gray)
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp))
            RenderBlocks(blocks = blocks, onAction = { action, title, subtitle ->
                onActionClick(action, title, subtitle)
            })
            Spacer(Modifier.height(16.dp))
        }
    }
}