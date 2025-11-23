package com.example.gowatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gowatch.navigation.Screen
import com.example.gowatch.ui.screens.DashboardScreen
import com.example.gowatch.ui.screens.MovieDetailScreen
import com.example.gowatch.ui.screens.RatingsScreen
import com.example.gowatch.ui.screens.SearchScreen
import com.example.gowatch.ui.screens.SplashScreen
import com.example.gowatch.ui.screens.WatchlistScreen
import com.example.gowatch.ui.theme.GoWatchTheme
import com.example.gowatch.viewmodel.MovieDetailViewModel
import com.example.gowatch.viewmodel.MovieViewModel

class MainActivity : ComponentActivity() {

    private val movieViewModel: MovieViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GoWatchTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // ✅ PERBAIKAN: Gunakan Screen.Splash.route sebagai startDestination
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Splash.route // <-- UBAH KE SPLASH
                    ) {

                        // 1. Tambahkan composable untuk Splash Screen
                        composable(Screen.Splash.route) {
                            // ✅ PRE-LOADING data sambil Splash Screen berjalan
                            LaunchedEffect(Unit) {
                                movieViewModel.loadMovies()
                            }

                            SplashScreen(
                                onTimeout = {
                                    // Setelah selesai, navigasi ke Dashboard dan hapus Splash dari back stack
                                    navController.popBackStack()
                                    navController.navigate(Screen.Dashboard.route)
                                }
                            )
                        }


                        // 2. Ubah destinasi Dashboard. Hapus semua logika Splash di dalamnya.
                        composable(Screen.Dashboard.route) {
                            // Hapus LaunchedEffect(Unit) yang lama karena sudah dipindahkan ke Splash Screen

                            DashboardScreen(
                                viewModel = movieViewModel,
                                onSearchClick = {
                                    navController.navigate(Screen.Search.route)
                                },
                                onMovieClick = { movieId ->
                                    navController.navigate(Screen.MovieDetail.createRoute(movieId))
                                },
                                onWatchlistClick = {
                                    navController.navigate(Screen.Watchlist.route)
                                },
                                onRatingsClick = {
                                    navController.navigate(Screen.Ratings.route)
                                }
                            )
                        }

                        composable(Screen.Search.route) {
                            SearchScreen(
                                viewModel = movieViewModel,
                                onBackClick = { navController.popBackStack() },
                                onMovieClick = { movieId ->
                                    navController.navigate(Screen.MovieDetail.createRoute(movieId))
                                }
                            )
                        }

                        composable(Screen.MovieDetail.route) { backStackEntry ->
                            val movieId = backStackEntry.arguments?.getString("movieId") ?: ""
                            val movie = movieViewModel.movies.value.find { it.id == movieId }
                                ?: movieViewModel.searchResults.value.find { it.id == movieId }

                            val movieDetailViewModel: MovieDetailViewModel = viewModel()

                            LaunchedEffect(movieId) {
                                if (movieId.isNotEmpty()) {
                                    movieDetailViewModel.loadMovieDetail(movieId)
                                }
                            }

                            MovieDetailScreen(
                                movie = movie,
                                onBackClick = { navController.popBackStack() },
                                onAddToWatchlist = { item ->
                                    movieViewModel.addToWatchlist(item)
                                },
                                onRateMovie = { ratedItem ->
                                    movieViewModel.rateMovie(
                                        movieId = ratedItem.movieId,
                                        rating = ratedItem.userRating,
                                        review = ratedItem.userReview
                                    )
                                },
                                viewModel = movieDetailViewModel
                            )
                        }

                        composable(Screen.Watchlist.route) {
                            WatchlistScreen(
                                viewModel = movieViewModel,
                                onBackClick = { navController.popBackStack() },
                                onMovieClick = { movieId ->
                                    navController.navigate(Screen.MovieDetail.createRoute(movieId))
                                }
                            )
                        }

                        composable(Screen.Ratings.route) {
                            RatingsScreen(
                                viewModel = movieViewModel,
                                onBackClick = { navController.popBackStack() },
                                onMovieClick = { movieId ->
                                    navController.navigate(Screen.MovieDetail.createRoute(movieId))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}