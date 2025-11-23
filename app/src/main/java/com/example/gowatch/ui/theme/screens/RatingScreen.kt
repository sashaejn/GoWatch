package com.example.gowatch.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gowatch.viewmodel.MovieViewModel

// RatingsScreen.kt - UPDATE PARAMETER
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingsScreen(
    viewModel: MovieViewModel, // ← PARAMETER PERTAMA
    onBackClick: () -> Unit,
    onMovieClick: (String) -> Unit
) {
    val ratedMovies by viewModel.watchedMovies.collectAsState()

    // DEBUG: Log perubahan data
    LaunchedEffect(ratedMovies) {
        println("📊 RATINGS SCREEN - Rated movies updated: ${ratedMovies.size} items")
        ratedMovies.forEach {
            println("   - ${it.title} (${it.userRating}/5)")
        }
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "My Ratings",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (ratedMovies.isEmpty()) {
            EmptyRatingsState()
        } else {
            RatingsList(
                ratedMovies = ratedMovies,
                onMovieClick = onMovieClick,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
private fun RatingsList(
    ratedMovies: List<com.example.gowatch.data.model.WatchlistItem>,
    onMovieClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(ratedMovies, key = { it.movieId }) { ratedMovie ->
            RatedMovieItem(
                ratedMovie = ratedMovie,
                onMovieClick = onMovieClick
            )
        }
    }
}

@Composable
private fun RatedMovieItem(
    ratedMovie: com.example.gowatch.data.model.WatchlistItem,
    onMovieClick: (String) -> Unit
) {
    Card(
        onClick = { onMovieClick(ratedMovie.movieId) },
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Movie Poster
            AsyncImage(
                model = ratedMovie.posterUrl,
                contentDescription = "Poster for ${ratedMovie.title}",
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp)
            )

            // Movie Info and Rating
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = ratedMovie.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rating Stars
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${ratedMovie.userRating}/5",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Review (if any)
                if (ratedMovie.userReview.isNotEmpty()) {
                    Text(
                        text = ratedMovie.userReview,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Date Rated
                Text(
                    text = "Rated on ${ratedMovie.addedDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun EmptyRatingsState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "No ratings",
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No Rated Movies Yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Rate movies you've watched to see them here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}