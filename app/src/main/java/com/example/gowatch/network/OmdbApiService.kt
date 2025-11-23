package com.example.gowatch.network

import com.example.gowatch.data.model.MovieDetail
import com.example.gowatch.data.model.OmdbResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OmdbApiService {

    @GET("/")
    suspend fun searchMovies(
        @Query("s") query: String,
        @Query("apikey") apiKey: String = "720c3666",
        @Query("page") page: Int = 1,
        @Query("type") type: String = "movie",
        @Query("y") year: String? = null
    ): Response<OmdbResponse>

    @GET("/")
    suspend fun getMovieDetail(
        @Query("i") imdbId: String,
        @Query("apikey") apiKey: String = "720c3666",
        @Query("plot") plot: String = "full"
    ): Response<MovieDetail> // ← PASTIKAN MovieDetail, bukan Screen.MovieDetail
    // Untuk get trending/popular movies (pake search dengan keyword umum)
    @GET("/")
    suspend fun getPopularMovies(
        @Query("s") query: String = "movie",
        @Query("apikey") apiKey: String = "720c3666",
        @Query("type") type: String = "movie"
    ): Response<OmdbResponse>
}

object OmdbApiInstance {
    private const val BASE_URL = "https://www.omdbapi.com/"

    private val client = okhttp3.OkHttpClient.Builder()
        .addInterceptor(okhttp3.logging.HttpLoggingInterceptor().apply {
            level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    val api: OmdbApiService by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .client(client)
            .build()
            .create(OmdbApiService::class.java)
    }
}