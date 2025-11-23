package com.example.gowatch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gowatch.data.model.Movie
import com.example.gowatch.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import com.example.gowatch.data.model.WatchlistItem

class MovieViewModel : ViewModel() {

    private val repository = MovieRepository()

    // State
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Movie>>(emptyList())
    val searchResults: StateFlow<List<Movie>> = _searchResults.asStateFlow()

    private val _watchlist = MutableStateFlow<List<WatchlistItem>>(emptyList())
    val watchlist: StateFlow<List<WatchlistItem>> = _watchlist.asStateFlow()

    private val _watchedMovies = MutableStateFlow<List<WatchlistItem>>(emptyList())
    val watchedMovies: StateFlow<List<WatchlistItem>> = _watchedMovies.asStateFlow()

    private val _selectedMovie = MutableStateFlow<Movie?>(null)
    val selectedMovie: StateFlow<Movie?> = _selectedMovie.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        println("🔄 MovieViewModel initialized")
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _movies.value = repository.getPopularMovies()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load movies: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchMovies(query: String) {
        if (query.length < 2) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                _searchResults.value = repository.searchMovies(query)
            } catch (e: Exception) {
                _errorMessage.value = "Search error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Overload function untuk kompatibilitas
    fun addToWatchlist(item: WatchlistItem) {
        addToWatchlist(item.movieId)
    }

    fun addToWatchlist(movieId: String) {
        viewModelScope.launch {
            try {
                val movie = movies.value.find { it.id == movieId }
                    ?: searchResults.value.find { it.id == movieId }

                if (movie != null) {
                    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                    val watchlistItem = WatchlistItem(
                        id = System.currentTimeMillis().toString(),
                        userId = "user123",
                        movieId = movieId,
                        title = movie.title,
                        posterUrl = movie.posterUrl,
                        userRating = 0f,
                        userReview = "",
                        addedDate = currentDate,
                        watchedDate = "",
                        isWatched = false,
                        isWatchlist = true
                    )

                    // Update watchlist
                    val currentWatchlist = _watchlist.value.toMutableList()
                    currentWatchlist.removeAll { it.movieId == movieId }
                    currentWatchlist.add(0, watchlistItem)
                    _watchlist.value = currentWatchlist

                    println("✅ WATCHLIST SUCCESS - ${movie.title} added to watchlist")

                    // ✅ TIDAK ADA SUCCESS MESSAGE - biarkan silent

                } else {
                    _errorMessage.value = "Movie not found for watchlist"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add to watchlist: ${e.message}"
            }
        }
    }

    fun rateMovie(movieId: String, rating: Float, review: String? = null) {
        viewModelScope.launch {
            try {
                val movie = movies.value.find { it.id == movieId }
                    ?: searchResults.value.find { it.id == movieId }

                if (movie != null) {
                    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                    val ratedItem = WatchlistItem(
                        id = System.currentTimeMillis().toString(),
                        userId = "user123",
                        movieId = movieId,
                        title = movie.title,
                        posterUrl = movie.posterUrl,
                        userRating = rating,
                        userReview = review ?: "",
                        addedDate = currentDate,
                        watchedDate = currentDate,
                        isWatched = true,
                        isWatchlist = false
                    )

                    println("🎯 Creating rated item: ${ratedItem.title} with rating ${ratedItem.userRating}")

                    // Update watchedMovies
                    val currentWatched = _watchedMovies.value.toMutableList()
                    currentWatched.removeAll { it.movieId == movieId }
                    currentWatched.add(0, ratedItem)
                    _watchedMovies.value = currentWatched

                    println("🎯 WatchedMovies update: ${_watchedMovies.value.size} items")

                    // Update watchlist jika movie ada di watchlist
                    val currentWatchlist = _watchlist.value.toMutableList()
                    var watchlistUpdated = false

                    for (i in currentWatchlist.indices) {
                        if (currentWatchlist[i].movieId == movieId) {
                            println("🔄 Updating existing watchlist item: ${currentWatchlist[i].title}")
                            currentWatchlist[i] = currentWatchlist[i].copy(
                                userRating = rating,
                                userReview = review ?: "",
                                isWatched = true,
                                watchedDate = currentDate
                            )
                            watchlistUpdated = true
                            break
                        }
                    }

                    if (watchlistUpdated) {
                        _watchlist.value = currentWatchlist
                    }

                    // ✅✅✅ PERBAIKAN: TIDAK ADA SUCCESS MESSAGE ✅✅✅
                    // Biarkan proses berjalan silent
                    // Success feedback sudah ditangani di MovieDetailScreen via snackbar

                    println("✅ RATING SUCCESS - ${movie.title} ($rating/5)")

                } else {
                    // ❗ HANYA tampilkan error message untuk kasus error
                    _errorMessage.value = "Movie not found for rating"
                }
            } catch (e: Exception) {
                // ❗ HANYA tampilkan error message untuk kasus error
                _errorMessage.value = "Failed to rate movie: ${e.message}"
            }
        }
    }
    // Fungsi bantuan untuk kompatibilitas

    fun removeFromWatchlist(movieId: String) {
        viewModelScope.launch {
            try {
                _watchlist.update { currentList ->
                    val filtered = currentList.filterNot { it.movieId == movieId }
                    println("🗑️ Removed from watchlist: $movieId")
                    filtered
                }
                _errorMessage.value = "Removed from watchlist"

                viewModelScope.launch {
                    delay(2000)
                    _errorMessage.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to remove from watchlist"
            }
        }
    }

    fun getMovieDetail(imdbId: String) {
        viewModelScope.launch {
            _errorMessage.value = "Movie detail feature coming soon!"
        }
    }

    fun selectMovie(movie: Movie) {
        _selectedMovie.value = movie
    }

    fun clearSelectedMovie() {
        _selectedMovie.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

    // Hapus fungsi yang tidak diperlukan
    // fun loadWatchlist() { ... } // ❌ HAPUS
    // fun loadWatchedMovies() { ... } // ❌ HAPUS
}