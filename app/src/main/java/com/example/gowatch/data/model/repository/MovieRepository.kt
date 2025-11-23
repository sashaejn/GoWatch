package com.example.gowatch.data.repository

import android.util.Log
import com.example.gowatch.data.model.Movie
import com.example.gowatch.data.model.MovieDetail
import com.example.gowatch.data.model.OmdbResponse
import com.example.gowatch.network.OmdbApiInstance
import kotlinx.coroutines.delay
import retrofit2.Response

class MovieRepository {

    suspend fun getMovieDetail(imdbId: String): MovieDetail {
        return try {
            Log.d("GoWatch", "🎬 Fetching movie detail for: $imdbId")
            val response = OmdbApiInstance.api.getMovieDetail(imdbId)

            if (response.isSuccessful) {
                val movieDetail = response.body()
                if (movieDetail != null) {
                    Log.d("GoWatch", "✅ Movie detail fetched: ${movieDetail.title}")
                    return movieDetail
                } else {
                    throw Exception("Movie detail not found")
                }
            } else {
                throw Exception("API error: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("GoWatch", "💥 Movie detail failed: ${e.message}")
            throw e
        }
    }

    suspend fun getPopularMovies(): List<Movie> {
        return try {
            Log.d("GoWatch", "🎞️ Fetching popular movies from OMDB...")

            // OMDB API tidak memiliki endpoint "popular", jadi kita gunakan search dengan query umum
            val popularQueries = listOf("avengers", "batman", "superman", "spider", "star wars", "marvel")
            val allMovies = mutableListOf<Movie>()

            for (query in popularQueries) {
                try {
                    val response = OmdbApiInstance.api.searchMovies(query)
                    if (response.isSuccessful) {
                        val movies = response.body()?.search ?: emptyList()
                        allMovies.addAll(movies)
                        Log.d("GoWatch", "✅ Found ${movies.size} movies for query: $query")
                    }
                    // Delay kecil untuk menghindari rate limit
                    delay(100)
                } catch (e: Exception) {
                    Log.e("GoWatch", "❌ Error fetching for query $query: ${e.message}")
                }
            }

            // Remove duplicates based on movie ID
            val uniqueMovies = allMovies.distinctBy { it.id }
            Log.d("GoWatch", "🎉 Successfully fetched ${uniqueMovies.size} unique popular movies")
            uniqueMovies
        } catch (e: Exception) {
            Log.e("GoWatch", "💥 Popular movies failed: ${e.message}")
            emptyList()
        }
    }

    suspend fun searchMovies(query: String): List<Movie> {
        return try {
            Log.d("GoWatch", "🔍 Searching OMDB for: $query")

            val response = OmdbApiInstance.api.searchMovies(query)
            val responseBody = response.body()

            Log.d("GoWatch", "📡 Response code: ${response.code()}")
            Log.d("GoWatch", "📦 Response body: $responseBody")

            if (response.isSuccessful && responseBody?.Response == "True") {
                val movies = responseBody.search ?: emptyList()
                Log.d("GoWatch", "✅ Search successful! Found ${movies.size} results")
                movies
            } else {
                val errorMsg = responseBody?.Error ?: "Unknown error"
                Log.e("GoWatch", "❌ Search failed: $errorMsg")

                // Fallback ke search yang lebih spesifik
                fallbackSearch(query)
            }
        } catch (e: Exception) {
            Log.e("GoWatch", "💥 Network error: ${e.message}")
            emptyList()
        }
    }

    private suspend fun fallbackSearch(query: String): List<Movie> {
        Log.d("GoWatch", "🔄 Trying fallback search for: $query")

        val fallbackTerms = listOf(
            "$query movie", "the $query", "$query 2023", "$query 2022"
        )

        for (term in fallbackTerms) {
            try {
                Log.d("GoWatch", "🔄 Trying: $term")
                val response = OmdbApiInstance.api.searchMovies(term)
                val responseBody = response.body()

                if (response.isSuccessful && responseBody?.Response == "True") {
                    val movies = responseBody.search ?: emptyList()
                    if (movies.isNotEmpty()) {
                        Log.d("GoWatch", "✅ Fallback success with: $term")
                        return movies
                    }
                }
            } catch (e: Exception) {
                continue
            }
        }

        Log.d("GoWatch", "❌ All fallback attempts failed")
        return emptyList()
    }
}