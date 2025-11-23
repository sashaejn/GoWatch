package com.example.gowatch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gowatch.ui.components.MovieCard
import com.example.gowatch.ui.components.SectionHeader
import com.example.gowatch.viewmodel.MovieViewModel
import com.example.gowatch.R
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.gowatch.ui.theme.GoWatchTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MovieViewModel,
    onSearchClick: () -> Unit,
    onMovieClick: (String) -> Unit,
    onWatchlistClick: () -> Unit,
    onRatingsClick: () -> Unit
) {
    val watchedMovies by viewModel.watchedMovies.collectAsState()
    val watchlist by viewModel.watchlist.collectAsState()
    val movies by viewModel.movies.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Refresh data when screen appears
    LaunchedEffect(Unit) {
        viewModel.loadMovies()
    }

    // Debug effects
    LaunchedEffect(watchedMovies) {
        println("📊 DASHBOARD - WatchedMovies: ${watchedMovies.size}")
    }

    LaunchedEffect(watchlist) {
        println("📊 DASHBOARD - Watchlist: ${watchlist.size}")
    }

    // MEMBUNGKUS DENGAN SCAFFOLD
    Scaffold(
        // Catatan: Karena Anda tidak menggunakan TopBar/BottomBar, kita hanya menggunakan Scaffold
        // untuk mendapatkan padding yang benar (insets).
    ) { paddingValues ->

        Column(
            modifier = Modifier
                // INI PERBAIKAN UTAMA: Menerapkan padding yang disediakan oleh Scaffold
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎬 GoWatch",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(id = R.string.welcome),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }

            // Search Bar
            OutlinedTextField(
                value = "",
                onValueChange = { /* Handled by click */ },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.search_hint),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onSearchClick() },
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                ),
                readOnly = true,
                enabled = false
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Loading State
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                // Hapus 'return@Column' karena sekarang berada di dalam Column yang di-Scaffold
                // dan mungkin perlu dibungkus dalam Box atau Column lain yang memiliki tinggi tetap
                // jika konten lain diharapkan tidak bergeser. Namun, untuk perbaikan ini, biarkan saja.
            }

            // Error State - Hanya tampilkan ERROR messages (bukan success)
            if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "😕",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorMessage ?: "Terjadi kesalahan",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    viewModel.clearError()
                                    viewModel.loadMovies()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Pindahkan seluruh logika konten di bawah sini

            // Trending Movies Section
            SectionHeader(
                title = stringResource(id = R.string.trending),
                onSeeAllClick = null
            )

            if (movies.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tidak ada film tersedia",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(movies.take(10)) { movie ->
                        MovieCard(
                            title = movie.title,
                            posterUrl = movie.posterUrl,
                            onClick = {
                                onMovieClick(movie.id)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // My Watchlist Section
            SectionHeader(
                title = stringResource(id = R.string.my_watchlist),
                itemCount = watchlist.size,
                onSeeAllClick = onWatchlistClick
            )

            if (watchlist.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tambahkan film ke watchlist untuk melihatnya di sini!",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(watchlist.take(10)) { item ->
                        MovieCard(
                            title = item.title,
                            posterUrl = item.posterUrl,
                            showWatchLabel = true,
                            onClick = {
                                onMovieClick(item.movieId)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // My Ratings Section
            SectionHeader(
                title = stringResource(id = R.string.my_ratings),
                itemCount = watchedMovies.size,
                onSeeAllClick = onRatingsClick
            )

            if (watchedMovies.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "⭐",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Belum Ada Film yang Di-rating",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rate film yang sudah ditonton untuk melihatnya di sini",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(watchedMovies.take(10)) { item ->
                        MovieCard(
                            title = item.title,
                            posterUrl = item.posterUrl,
                            showUserRating = item.userRating.toDouble(),
                            onClick = {
                                onMovieClick(item.movieId)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        } // End Column
    } // End Scaffold
}

@Preview(name = "Dashboard - Empty", showBackground = true)
@Preview(name = "Dashboard - Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DashboardScreenPreview() {
    GoWatchTheme {
        DashboardScreen(
            viewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
            onSearchClick = {},
            onMovieClick = {},
            onWatchlistClick = {},
            onRatingsClick = {}
        )
    }
}