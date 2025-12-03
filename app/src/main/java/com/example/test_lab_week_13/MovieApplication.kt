package com.example.test_lab_week_13

import android.app.Application
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.example.test_lab_week_13.api.MovieService

class MovieApplication : Application() {
    lateinit var movieRepository: MovieRepository
    override fun onCreate() {
        super.onCreate()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        val movieService = retrofit.create(
            MovieService::class.java
        )
// create a MovieRepository instance
        movieRepository = MovieRepository(movieService)
    }
}
