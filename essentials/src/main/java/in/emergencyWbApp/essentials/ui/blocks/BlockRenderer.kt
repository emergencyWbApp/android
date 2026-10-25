package `in`.emergencyWbApp.essentials.ui.blocks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import `in`.emergencyWbApp.essentials.ui.DynamicIcon
import `in`.emergencyWbApp.essentials.ui.IconVariant
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RenderBlocks(
    blocks: List<Block>,
    onAction: (action: String, title: String, subtitle: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        blocks.forEach { block ->
            when (block) {
                is Block.RadialOrbit -> RadialOrbitBlock(block, onAction)
                is Block.CardGridSection -> CardGridSectionBlock(block, onAction)
                is Block.HeroCall -> HeroCallBlock(block, onAction)
                is Block.LocationCard -> LocationCardBlock(block, onAction)
                is Block.SectionHeader -> SectionHeaderBlock(block)
                is Block.ActionGrid -> ActionGridBlock(block, onAction)
                is Block.Banner -> BannerBlock(block)
                is Block.Unknown -> { /* Ignore */ }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────
// 1. RADIAL ORBIT
// ──────────────────────────────────────────────────────────
@Composable
private fun RadialOrbitBlock(
    block: Block.RadialOrbit,
    onAction: (action: String, title: String, subtitle: String) -> Unit) {
    val centerColor = try { Color(block.center.color?.toColorInt() ?: 0xFFDC2626.toInt()) } catch (e: Exception) { Color.Red }
    val orbitColors = block.orbits.map { try { Color(it.color?.toColorInt() ?: 0xFF2563EB.toInt()) } catch (e: Exception) { Color.Blue } }

    // 📏 ডিজাইনের সাথে ১০০% ম্যাচ করার জন্য মাপ (Size)
    val boxHeight = 300.dp
    val circleSize = 260.dp
    val centerCardSize = 130.dp
    val orbitCardWidth = 85.dp
    val orbitCardHeight = 95.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(boxHeight),
        contentAlignment = Alignment.Center
    ) {
        // ── ১. ১১২ বাটনের মাল্টি-লেয়ার গ্লো (Soft Glow) ──
        // বাইরের হালকা আভা
        Box(
            modifier = Modifier
                .size(centerCardSize + 45.dp)
                .clip(CircleShape)
                .blur(30.dp)
                .background(centerColor.copy(alpha = 0.15f), CircleShape)
        )
        // ভেতরের গাঢ় আভা
        Box(
            modifier = Modifier
                .size(centerCardSize + 20.dp)
                .clip(CircleShape)
                .blur(15.dp)
                .background(centerColor.copy(alpha = 0.25f), CircleShape)
        )

        // ── ২. অরবিটের ভাঙা ভাঙা সার্কেলগুলো (Canvas) ──
        Canvas(modifier = Modifier.size(circleSize)) {
            val stroke = 1.5.dp.toPx()
            // টপ-লেফট (লাল) - BLOOD
            drawArc(color = Color(0xFFDC2626), startAngle = 180f, sweepAngle = 90f, useCenter = false, style = Stroke(stroke))
            // টপ-রাইট (নীল) - POLICE
            drawArc(color = Color(0xFF2563EB), startAngle = 270f, sweepAngle = 90f, useCenter = false, style = Stroke(stroke))
            // বটম-রাইট (লাল) - FIRE
            drawArc(color = Color(0xFFDC2626), startAngle = 0f, sweepAngle = 90f, useCenter = false, style = Stroke(stroke))
            // বটম-লেফট (সবুজ) - AMBULANCE
            drawArc(color = Color(0xFF16A34A), startAngle = 90f, sweepAngle = 90f, useCenter = false, style = Stroke(stroke))
        }

        // ── ৩. সেন্টার কার্ড (১১২) ──
        Surface(
            onClick = { block.center.action?.let { action ->
                // 🎯 এখানে Title এবং Number পাস করা হলো
                onAction(action, block.center.title, block.center.number)
            }
                      },
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 4.dp,
            border = BorderStroke(2.dp, centerColor), // 🎯 ছবির মতো মোটা লাল বর্ডার
            modifier = Modifier.size(centerCardSize)
        ) {
            // 🎯 বর্ডারের ভেতরে সাদা জায়গা (Gap) তৈরি করার জন্য padding
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, centerColor, CircleShape) // 🎯 ভেতরের পাতলা বর্ডার
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = block.center.number,
                        color = centerColor,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 1.sp
                    )
                    Text(
                        text = block.center.title,
                        color = centerColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        lineHeight = 1.sp
                    )

                    Spacer(Modifier.height(5.dp))

                    // 🎯 ভেতরের ছোট কল বাটন (লাল শ্যাডোসহ)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(centerColor.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        DynamicIcon(
                            name = block.center.btnIcon ?: "phone",
                            variant = IconVariant.OUTLINE,
                            tint = Color.DarkGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // ── ৪. অরবিট কার্ডগুলো ──
        val radius = (circleSize.value / 2) + 12f
        val angles = listOf(225f, 315f, 45f, 135f)

        block.orbits.take(4).forEachIndexed { index, orbit ->
            val angleRad = Math.toRadians(angles[index].toDouble())
            val xOffset = (radius * cos(angleRad)).dp
            val yOffset = (radius * sin(angleRad)).dp
            val orbitColor = orbitColors.getOrElse(index) { Color.Blue }

            Box(modifier = Modifier.offset(x = xOffset, y = yOffset)) {
                Surface(
                    onClick = { orbit.action?.let { action ->
                        // 🎯 অ্যাকশন থেকে নাম্বার বের করে পাস করা হলো
                        val number = action.removePrefix("tel:").trim()
                        onAction(action, orbit.title, number)
                    }  },
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 6.dp,
                    modifier = Modifier.size(orbitCardWidth, orbitCardHeight)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize().padding(2.dp)
                    ) {
                        DynamicIcon(
                            name = orbit.icon ?: "",
                            variant = IconVariant.OUTLINE,
                            tint = orbitColor,
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = orbit.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                            lineHeight = 10.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(orbitColor.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            DynamicIcon(
                                name = orbit.btnIcon ?: "phone",
                                variant = IconVariant.OUTLINE,
                                tint = Color.DarkGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
// ──────────────────────────────────────────────────────────
// 2. CARD GRID SECTION
// ──────────────────────────────────────────────────────────
@Composable
private fun CardGridSectionBlock(
    block: Block.CardGridSection,
    onAction: (action: String, title: String, subtitle: String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = block.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                letterSpacing = 0.5.sp
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "See all",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
        }

        val columns = 2
        block.items.chunked(columns).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
            ) {
                rowItems.forEach { item ->
                    val itemColor = try { Color(item.color?.toColorInt() ?: 0xFF2563EB.toInt()) } catch (e: Exception) { Color.Blue }

                    Surface(
                        onClick = {
                            item.action?.let { action ->
                                val number = action.removePrefix("tel:").trim()
                                // সাবটাইটেলে যদি "24/7 support" থাকে, তবে সেটা না পাঠিয়ে নাম্বারটা পাঠাই
                                onAction(action, item.title, number)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        shadowElevation = 1.dp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp) // সার্কেল সাইজ বড় করা হলো (26 থেকে 28)
                                    .background(itemColor.copy(alpha = 0.12f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                DynamicIcon(
                                    name = item.icon ?: "",
                                    variant = IconVariant.OUTLINE,
                                    tint = itemColor,
                                    modifier = Modifier.size(20.dp) // মেইন আইকন বড় (14 থেকে 16)
                                )
                            }
                            Spacer(Modifier.width(8.dp))

                            // 🎯 টেক্সট এরিয়াতে lineHeight ব্যবহার করে গ্যাপ কমানো হলো
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1F2937),
                                    lineHeight = 13.sp // টাইটেলের নিচে এক্সট্রা গ্যাপ বাদ দিল
                                )
                                if (!item.subtitle.isNullOrBlank()) {
                                    Text(
                                        text = item.subtitle,
                                        fontSize = 11.sp,
                                        color = Color(0xFF6B7280),
                                        lineHeight = 10.sp // সাবটাইটেল উপরে টাইটেলের সাথে লেগে থাকবে
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(32.dp) // অ্যাকশন বাটন সাইজ বড় (22 থেকে 24)
                                    .background(itemColor.copy(alpha = 0.12f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                DynamicIcon(
                                    name = item.btnIcon ?: "",
                                    variant = IconVariant.OUTLINE,
                                    tint = Color.DarkGray,
                                    modifier = Modifier.size(16.dp) // অ্যাকশন আইকন বড় (10 থেকে 12)
                                )
                            }
                        }
                    }
                }
                repeat(columns - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────
// ৩. পুরনো ব্লকগুলো
// ──────────────────────────────────────────────────────────
@Composable
private fun HeroCallBlock(block: Block.HeroCall, onAction: (String, String, String) -> Unit) { /* পুরনো ডিজাইন */ }
@Composable
private fun LocationCardBlock(block: Block.LocationCard, onAction: (String, String, String) -> Unit) { /* পুরনো ডিজাইন */ }
@Composable
private fun SectionHeaderBlock(block: Block.SectionHeader) { /* পুরনো ডিজাইন */ }
@Composable
private fun ActionGridBlock(block: Block.ActionGrid, onAction: (String, String, String) -> Unit) { /* পুরনো ডিজাইন */ }
@Composable
private fun BannerBlock(block: Block.Banner) { /* পুরনো ডিজাইন */ }