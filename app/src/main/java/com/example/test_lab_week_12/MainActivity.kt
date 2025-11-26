package com.example.test_lab_week_12

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.test_lab_week_12.model.Movie
import com.google.android.material.snackbar.Snackbar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel

// *** MISSING IMPORTS ADDED ***
import androidx.lifecycle.lifecycleScope // Required for lifecycleScope.launch
import androidx.lifecycle.repeatOnLifecycle // Required for repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.launch
// ****************************

class MainActivity : AppCompatActivity() {
    private val movieAdapter by lazy {
        MovieAdapter(object : MovieAdapter.MovieClickListener {
            override fun onMovieClick(movie: Movie) {
                openMovieDetails(movie)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.movie_list)
        recyclerView.adapter = movieAdapter

        // 1. ViewModel Setup
        val movieRepository = (application as MovieApplication).movieRepository
        val movieViewModel = ViewModelProvider(
            this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    // Suppress warning for unsafe cast
                    @Suppress("UNCHECKED_CAST")
                    return MovieViewModel(movieRepository) as T
                }
            })[MovieViewModel::class.java]

        // 2. Coroutine-based Collection for StateFlows
        lifecycleScope.launch {
            // repeatOnLifecycle ensures the block runs only when the Lifecycle is STARTED
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Launch coroutine to collect movies
                launch {
                    movieViewModel.popularMovies.collect { movies ->
                        // The movies list from StateFlow is added to the adapter
                        movieAdapter.addMovies(movies)
                    }
                }

                // Launch coroutine to collect errors
                launch {
                    movieViewModel.error.collect { error ->
                        // If an error message is present, show the Snackbar
                        if (error.isNotEmpty()) {
                            Snackbar.make(
                                recyclerView, error, Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun openMovieDetails(movie: Movie) {
        val intent = Intent(this, DetailsActivity::class.java).apply {
            putExtra(DetailsActivity.EXTRA_TITLE, movie.title)
            putExtra(DetailsActivity.EXTRA_RELEASE, movie.releaseDate)
            putExtra(DetailsActivity.EXTRA_OVERVIEW, movie.overview)
            putExtra(DetailsActivity.EXTRA_POSTER, movie.posterPath)
        }
        startActivity(intent)
    }
}