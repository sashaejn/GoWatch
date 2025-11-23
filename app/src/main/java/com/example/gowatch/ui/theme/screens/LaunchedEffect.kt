package com.example.gowatch.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing // ✅ GANTI INI
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gowatch.R

@Composable
fun SplashScreen(onTimeout: () -> Unit) {

    val scale = remember { Animatable(0.5f) }
    val splashDuration = 2000L // 2 detik

    // Animasi: bounce effect (Sekarang menggunakan easing yang stabil)
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                // ✅ PENGGANTIAN: Gunakan FastOutSlowInEasing (Easing yang paling stabil)
                easing = FastOutSlowInEasing
            )
        )
        // Tunggu hingga durasi splash screen selesai
        kotlinx.coroutines.delay(splashDuration - 800)
        // Pindah ke layar berikutnya
        onTimeout()
    }
    // ... (Sisa kode Column tetap sama)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo Aplikasi
        Text(
            text = "🎬 GoWatch",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .scale(scale.value) // Menerapkan animasi scale
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Slogan Aplikasi
        Text(
            text = stringResource(id = R.string.app_slogan), // Mengoreksi R.string._app_slogan ke R.string.app_slogan
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}