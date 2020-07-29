package io.github.kunal26das.yify.source

import android.annotation.SuppressLint
import android.util.Log
import androidx.essentials.core.KoinComponent.inject
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.github.kunal26das.yify.source.models.Movie
import io.github.kunal26das.yify.source.repositories.MovieRepository


/**
 * Created by kunal on 27-12-2019.
 */

class Yify : PageKeyedDataSource<Int, Movie>() {

    private val movieRepository: MovieRepository by inject()

    val dataSourceFactory = object : DataSource.Factory<Int, Movie>() {
        override fun create(): DataSource<Int?, Movie> {
            return this@Yify
        }
    }

    @SuppressLint("CheckResult")
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Movie>
    ) {
        val page = 1
        movieRepository.getMovies(page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("Page $page", "Success")
                callback.onResult(it.movieData?.movies as MutableList<Movie>, null, page + 1)
            }, {
                Log.e("Error", it.message!!)
                loadInitial(params, callback)
            })
    }

    @SuppressLint("CheckResult")
    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        val page = params.key + 1
        movieRepository.getMovies(page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("Page $page", "Success")
                callback.onResult(it.movieData?.movies as MutableList<Movie>, page)
            }, {
                Log.e("Error", it.message!!)
                loadAfter(params, callback)
            })
    }

    @SuppressLint("CheckResult")
    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        val page = params.key - 1
        movieRepository.getMovies(page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("Page $page", "Success")
                callback.onResult(it.movieData?.movies as MutableList<Movie>, page)
            }, {
                Log.e("Error", it.message!!)
                loadAfter(params, callback)
            })
    }
}