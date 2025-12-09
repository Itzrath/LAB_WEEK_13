package com.example.test_lab_week_13

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.test_lab_week_13.api.MovieService
import com.example.test_lab_week_13.database.MovieDao
import com.example.test_lab_week_13.database.MovieDatabase
import com.example.test_lab_week_13.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class  MovieRepository(private val movieService: MovieService,private val movieDatabase: MovieDatabase) {
    private val apiKey = "284492c3e93ef3f8627225340fade7f8"
    fun fetchMovies(): Flow<List<Movie>> {
        return flow {
            // Check if there are movies saved in the database
            val movieDao: MovieDao = movieDatabase.movieDao()
            val savedMovies = movieDao.getMovies()
            // If there are no movies saved in the database,
            // fetch the list of popular movies from the API
            if(savedMovies.isEmpty()) {
                val movies = movieService.getPopularMovies(apiKey).results
                // save the list of popular movies to the database
                movieDao.addMovies(movies)
                // emit the list of popular movies from the API
                emit(movies)
            } else {
                // If there are movies saved in the database,
                // emit the list of saved movies from the database
                emit(savedMovies)
            }
        }.flowOn(Dispatchers.IO)
    }
    suspend fun fetchMoviesFromNetwork() {
        val movieDao: MovieDao = movieDatabase.movieDao()
        try {
            val popularMovies = movieService.getPopularMovies(apiKey)
            val moviesFetched = popularMovies.results
            movieDao.addMovies(moviesFetched)
        } catch (exception: Exception) {
            Log.d(
                "MovieRepository",
                "An error occurred: ${exception.message}"
            )
        }
    }
}