package com.example.test_lab_week_13

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.test_lab_week_13.model.Movie
import com.google.android.material.snackbar.Snackbar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import com.example.test_lab_week_13.databinding.ActivityMainBinding // Assuming Data Binding class name
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest // Used for collecting StateFlow

// NOTE: You must create/import the following classes in your project:
// - MovieAdapter (Custom RecyclerView.Adapter)
// - MovieApplication (Custom Application class with MovieRepository)
// - MovieViewModel (ViewModel class holding popularMovies: StateFlow<List<Movie>>)
// - DetailsActivity (Activity to show movie details)
// - Movie (Data class/model)


class MainActivity : AppCompatActivity() {

    // Lazy initialization of the adapter
    private val movieAdapter by lazy {
        MovieAdapter(object : MovieAdapter.MovieClickListener {
            override fun onMovieClick(movie: Movie) {
                openMovieDetails(movie)
            }
        })
    }

    // Lazy initialization of the ViewModel
    private val movieViewModel by lazy {
        val movieRepository = (application as MovieApplication).movieRepository
        ViewModelProvider(
            this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return MovieViewModel(movieRepository) as T
                }
            })[MovieViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use DataBindingUtil to inflate the layout and get the binding object
        val binding: ActivityMainBinding = DataBindingUtil
            .setContentView(this, R.layout.activity_main)

        // Set up RecyclerView with the adapter
        binding.movieList.adapter = movieAdapter

        // Set the ViewModel and lifecycle owner for Data Binding
        binding.viewModel = movieViewModel
        binding.lifecycleOwner = this

        // Since we are using app:list="@{viewModel.popularMovies}" in the XML,
        // the custom Binding Adapter will automatically handle the list update
        // when popularMovies changes. We don't need manual observation here
        // for list data, but we can observe for other state changes if needed.

        // Example: Observation for displaying a loading state or error message
        lifecycleScope.launch {
            // repeatOnLifecycle ensures the block runs only when the Lifecycle is STARTED
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Here, you would observe any loading/error state if they were defined in the ViewModel
                // For instance, observing a loading StateFlow:
                /*
                movieViewModel.isLoading.collectLatest { isLoading ->
                    // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                }
                */
            }
        }
    }

    /**
     * Helper function to start the DetailsActivity with movie information.
     */
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