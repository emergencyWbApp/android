package `in`.emergencyWbApp.essentials.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun MeshGradientBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)) // বেস হালকা সাদা/গ্রে
    ) {
        // ১. বাম দিকের উপরের নীল আভা
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF93C5FD).copy(alpha = 0.4f), Color.Transparent),
                        center = Offset(0f, 0f),
                        radius = 800f
                    )
                )
        )

        // ২. ডান দিকের নিচের লাল/পিঙ্ক আভা
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFFFCA5A5).copy(alpha = 0.4f), Color.Transparent),
                        center = Offset(1000f, 1800f),
                        radius = 900f
                    )
                )
        )

        // ৩. মাঝখানের হালকা পার্পল আভা
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFFE0E7FF).copy(alpha = 0.5f), Color.Transparent),
                        center = Offset(500f, 500f),
                        radius = 600f
                    )
                )
        )

        // মূল কন্টেন্ট
        content()
    }
}