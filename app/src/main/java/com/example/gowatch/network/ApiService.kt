package com.example.gowatch.network

import com.example.gowatch.data.model.Movie
import com.example.gowatch.data.model.WatchlistItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

    // Movies endpoints - FIXED
    @GET("movies")
    suspend fun getMovies(): Response<List<Movie>>

    @GET("movies/{id}")
    suspend fun getMovieDetail(@Path("id") id: String): Response<Movie>

    // Search dengan filter title - FIXED
    @GET("movies")
    suspend fun searchMovies(@Query("title") query: String): Response<List<Movie>>

    // Watchlist endpoints - FIXED
    @GET("watchlists")
    suspend fun getWatchlists(): Response<List<WatchlistItem>>

    @GET("watchlists")
    suspend fun getWatchlistByUser(@Query("userId") userId: String): Response<List<WatchlistItem>>

    @POST("watchlists")
    suspend fun addToWatchlist(@Body watchlistItem: WatchlistItem): Response<WatchlistItem>

    @PUT("watchlists/{id}")
    suspend fun updateWatchlist(@Path("id") id: String, @Body watchlistItem: WatchlistItem): Response<WatchlistItem>

    @DELETE("watchlists/{id}")
    suspend fun removeFromWatchlist(@Path("id") id: String): Response<Void>

    @GET("watchlists")
    suspend fun getWatchedMovies(
        @Query("userId") userId: String,
        @Query("isWatched") isWatched: Boolean = true
    ): Response<List<WatchlistItem>>
}