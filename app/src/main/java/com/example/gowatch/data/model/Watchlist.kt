package com.example.gowatch.data.model

import com.google.gson.annotations.SerializedName

data class WatchlistItem(
    @SerializedName("id") val id: String? = null,
    @SerializedName("userId") val userId: String = "user123",
    @SerializedName("movieId") val movieId: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("posterUrl") val posterUrl: String = "",
    @SerializedName("userRating") val userRating: Float = 0f,
    @SerializedName("userReview") val userReview: String = "",
    @SerializedName("addedDate") val addedDate: String = "",
    @SerializedName("watchedDate") val watchedDate: String = "",
    @SerializedName("isWatched") val isWatched: Boolean = false,
    @SerializedName("isWatchlist") val isWatchlist: Boolean = true
)
