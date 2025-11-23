package com.example.gowatch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gowatch.data.model.Movie
import com.example.gowatch.data.model.MovieDetail
import com.example.gowatch.ui.components.RatingBar
import com.example.gowatch.viewmodel.MovieDetailViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.CircularProgressIndicator
import com.example.gowatch.data.model.WatchlistItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.gestures.detectTapGestures


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    movie: Movie?,
    onBackClick: () -> Unit,
    onAddToWatchlist: (WatchlistItem) -> Unit,
    onRateMovie: (WatchlistItem) -> Unit,
    viewModel: MovieDetailViewModel
) {
    var userRating by remember { mutableStateOf(0f) }
    var showRatingDialog by remember { mutableStateOf(false) }

    // State untuk kontrol visibilitas tombol
    var showFloatingButtons by remember { mutableStateOf(false) }
    var lastInteractionTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Collect state dari ViewModel
    val movieDetail by viewModel.movieDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Fallback jika movie null
    val currentMovie = movie ?: return

    // Fungsi untuk menangani interaksi user
    fun handleUserInteraction() {
        lastInteractionTime = System.currentTimeMillis()
        showFloatingButtons = true
    }

    // Auto hide FAB setelah 1 detik tanpa interaksi
    LaunchedEffect(showFloatingButtons, lastInteractionTime) {
        if (showFloatingButtons) {
            val currentTime = System.currentTimeMillis()
            val timeSinceLastInteraction = currentTime - lastInteractionTime

            if (timeSinceLastInteraction > 1000) {
                showFloatingButtons = false
            } else {
                // Schedule hide after remaining time
                delay(1000 - timeSinceLastInteraction)
                showFloatingButtons = false
            }
        }
    }

    // Load movie detail ketika screen pertama kali dibuka
    LaunchedEffect(currentMovie.id) {
        viewModel.loadMovieDetail(currentMovie.id)
    }

    // Tampilkan error dialog jika ada error
    if (error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Error") },
            text = { Text(error!!) },
            confirmButton = {
                Button(onClick = { viewModel.clearError() }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ) {
                    Text(
                        text = data.visuals.message,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = currentMovie.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
        },
        floatingActionButton = {
            // Hanya tampilkan FAB jika showFloatingButtons true
            if (showFloatingButtons) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Row(
                        modifier = Modifier
                            .width(IntrinsicSize.Max),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ExtendedFloatingActionButton(
                            onClick = {
                                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                                    Date()
                                )
                                val watchlistItem = WatchlistItem(
                                    movieId = currentMovie.id,
                                    title = currentMovie.title,
                                    posterUrl = currentMovie.posterUrl,
                                    addedDate = currentDate,
                                    isWatchlist = true
                                )
                                onAddToWatchlist(watchlistItem)
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Ditambahkan ke Tonton Nanti",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add to watchlist",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Watchlist",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }

                        ExtendedFloatingActionButton(
                            onClick = { showRatingDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            containerColor = MaterialTheme.colorScheme.secondary
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Rate movie",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Rate",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        // Tampilkan loading indicator
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        // Container utama dengan deteksi interaksi
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { handleUserInteraction() },
                        onPress = { handleUserInteraction() }
                    )
                }
        ) {
            // Movie Backdrop dengan deteksi interaksi
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { handleUserInteraction() },
                            onPress = { handleUserInteraction() }
                        )
                    }
            ) {
                AsyncImage(
                    model = currentMovie.posterUrl,
                    contentDescription = "Poster for ${currentMovie.title}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    contentScale = ContentScale.Crop
                )

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.background
                                ),
                                startY = 180f,
                                endY = 280f
                            )
                        )
                )
            }

            // Movie Info dengan deteksi interaksi
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { handleUserInteraction() },
                            onPress = { handleUserInteraction() }
                        )
                    }
            ) {
                // Title and Year
                Text(
                    text = "${currentMovie.title} (${currentMovie.year})",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tampilkan detail jika available, jika tidak tampilkan info basic
                if (movieDetail != null) {
                    DisplayMovieDetail(movieDetail!!)
                } else {
                    // Fallback UI jika detail tidak tersedia
                    DisplayBasicMovieInfo(currentMovie)
                }
            }
        }
    }

    // Rating Dialog
    if (showRatingDialog) {
        AlertDialog(
            onDismissRequest = { showRatingDialog = false },
            title = { Text("Rate ${currentMovie.title}") },
            text = {
                Column {
                    Text(
                        "How would you rate this movie?",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    RatingBar(
                        rating = userRating,
                        onRatingChange = { userRating = it },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Rating: ${userRating.toInt()}/5",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (userRating > 0) {
                            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                            val ratedItem = WatchlistItem(
                                movieId = currentMovie.id,
                                title = currentMovie.title,
                                posterUrl = currentMovie.posterUrl,
                                userRating = userRating,
                                userReview = "",
                                addedDate = currentDate,
                                isWatched = true
                            )

                            onRateMovie(ratedItem)

                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Berhasil memberi rating",
                                    duration = SnackbarDuration.Short
                                )
                            }

                            showRatingDialog = false
                            userRating = 0f
                        }
                    },
                    enabled = userRating > 0
                ) {
                    Text("Submit Rating")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRatingDialog = false
                    userRating = 0f
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DisplayMovieDetail(movieDetail: MovieDetail) {
    // Basic Info Row
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // IMDb Rating
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Rating",
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${movieDetail.imdbRating}/10",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Runtime
        Text(
            text = movieDetail.runtime,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )

        // Rated
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = movieDetail.rated,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Genres
    Text(
        text = movieDetail.genre,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Medium
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Release Date
    Text(
        text = "Released: ${movieDetail.released}",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Plot
    Text(
        text = movieDetail.plot,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3
    )

    Spacer(modifier = Modifier.height(20.dp))

    // Additional Info
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InfoRow("Director:", movieDetail.director)
        InfoRow("Cast:", movieDetail.actors)
        InfoRow("Writer:", movieDetail.writer)
        InfoRow("Language:", movieDetail.language)

        if (!movieDetail.awards.isNullOrEmpty() && movieDetail.awards != "N/A") {
            InfoRow("Awards:", movieDetail.awards)
        }
    }
}

@Composable
private fun DisplayBasicMovieInfo(movie: Movie) {
    Column {
        // Basic Info dari Movie
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = movie.type.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )

            Text(
                text = movie.year,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "IMDb ID: ${movie.id}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Loading detailed information...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    if (value.isNotEmpty() && value != "N/A") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.width(80.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
                modifier = Modifier.weight(1f)
            )
        }
    }
}