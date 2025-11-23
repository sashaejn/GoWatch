package com.example.gowatch.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.tooling.preview.Preview
import com.example.gowatch.ui.theme.GoWatchTheme

// MovieCard.kt - PERBAIKAN UKURAN CARD
@Composable
fun MovieCard(
    title: String,
    posterUrl: String,
    showWatchLabel: Boolean = false,
    showUserRating: Double? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(120.dp)
            .height(240.dp) // <--- BARIS INI DITAMBAHKAN UNTUK UKURAN TETAP
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxHeight()) { // Pastikan kolom mengisi tinggi Card
            AsyncImage(
                model = posterUrl,
                contentDescription = "Poster of $title",
                modifier = Modifier
                    .height(160.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )

            // Gunakan Box dengan weight untuk mendorong rating/label ke bawah (optional)
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxHeight() // Mengisi sisa ruang tinggi Card (240dp - 160dp)
            ) {
                // Judul
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis, // Titik-titik akan muncul jika lebih dari 2 baris
                    fontWeight = FontWeight.Medium,
                    lineHeight = MaterialTheme.typography.labelMedium.lineHeight * 0.9
                )

                // Spacer yang akan mendorong konten di bawahnya ke paling bawah
                Spacer(modifier = Modifier.weight(1f))

                // Label atau Rating
                when {
                    showWatchLabel -> {
                        Text(
                            text = "WATCHLIST",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    showUserRating != null && showUserRating > 0 -> {
                        Text(
                            text = "★ ${"%.1f".format(showUserRating)}/5",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    // Jika tidak ada label/rating, tetap gunakan Spacer untuk mengisi ruang
                    else -> {
                        Spacer(modifier = Modifier.height(0.dp))
                    }
                }
            }
        }
    }
}


@Preview(name = "Movie Card - Normal", showBackground = true)
@Preview(name = "Movie Card - Watchlist", showBackground = true)
@Preview(name = "Movie Card - Rated", showBackground = true)
@Composable
fun MovieCardPreviews() {
    GoWatchTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Normal movie card
            MovieCard(
                title = "The Avengers: The Longest Title Ever Created In The History Of Cinema",
                posterUrl = "https://image.tmdb.org/t/p/w500/cezWGskPY5x7GaglTTRN4Fugfb8.jpg",
                onClick = {}
            )

            // Watchlist card
            MovieCard(
                title = "Spider-Man",
                posterUrl = "https://image.tmdb.org/t/p/w500/1g0dhYtq4irTY1GPXvft6k4YLjm.jpg",
                showWatchLabel = true,
                onClick = {}
            )

            // Rated card
            MovieCard(
                title = "Inception",
                posterUrl = "https://image.tmdb.org/t/p/w500/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg",
                showUserRating = 4.5,
                onClick = {}
            )
        }
    }
}