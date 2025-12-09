package com.example.test_lab_week_13

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MovieWorker(
    private val context: Context, params: WorkerParameters
) : Worker(context, params) {
    // doWork() is called in a background thread
    // this is where you put the code that you want to run
    override fun doWork(): Result {
        // get a reference to the repository
        val movieRepository = (context as MovieApplication)
            .movieRepository
        // launch a coroutine in the IO thread
        CoroutineScope(Dispatchers.IO).launch {
            movieRepository.fetchMoviesFromNetwork()
        }
        return Result.success()
    }
}