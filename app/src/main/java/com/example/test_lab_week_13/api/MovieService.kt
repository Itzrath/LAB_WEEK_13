package com.example.test_lab_week_13.api

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.test_lab_week_13.model.PopularMoviesResponse

interface MovieService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String
    ): PopularMoviesResponse
}