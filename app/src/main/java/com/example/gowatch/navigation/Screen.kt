package com.example.gowatch.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Dashboard : Screen("dashboard")
    object Search : Screen("search")
    object MovieDetail : Screen("movie_detail/{movieId}") {
        fun createRoute(movieId: String) = "movie_detail/$movieId"
    }
    object Watchlist : Screen("watchlist")
    object Ratings : Screen("ratings")
    object RatingForm : Screen("rating_form/{watchlistId}") {
        fun createRoute(watchlistId: String) = "rating_form/$watchlistId"
    }
}
