// MovieDetailViewModel.kt - UBAH MENJADI SEPERTI INI
package com.example.gowatch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gowatch.data.model.MovieDetail
import com.example.gowatch.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieDetailViewModel : ViewModel() {
    private val repository = MovieRepository() // ← TAMBAH INI
    private val _movieDetail = MutableStateFlow<MovieDetail?>(null)
    val movieDetail: StateFlow<MovieDetail?> = _movieDetail.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadMovieDetail(imdbId: String) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                // PANGGIL REPOSITORY UNTUK DAPATKAN DATA DETAIL
                val detail = repository.getMovieDetail(imdbId)
                _movieDetail.value = detail
            } catch (e: Exception) {
                _error.value = "Failed to load movie details: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}