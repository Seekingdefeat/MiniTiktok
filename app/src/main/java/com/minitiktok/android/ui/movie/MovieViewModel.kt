package com.minitiktok.android.ui.movie

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.minitiktok.android.logic.Repository
import com.minitiktok.android.logic.model.MovieEntity
import com.minitiktok.android.logic.model.Version

class MovieViewModel : ViewModel() {
    class MovieCondition(var type: Int, var version: Int? = null)
    class VersionCondition(var type: Int, var version: Version? = null)

    private var movieLiveData = MutableLiveData<MovieCondition>()

    private var versionLiveData = MutableLiveData<VersionCondition>()

    val movieList = ArrayList<MovieEntity>()

    val versionList = ArrayList<Version>()

    var type: Int = 1

    var version: Int? = null

    val activityTime = MutableLiveData<String>()

    val moviesLiveData = Transformations.switchMap(movieLiveData) {
        Repository.getMovies(it.type, it.version)
    }

    val versionsLiveData = Transformations.switchMap(versionLiveData) {
        versionLiveData.value?.let { it1 -> Repository.getVersions(it1.type, it1.version) }
    }

    fun changeMovie(condition: MovieCondition) {
        if (condition.version != null && condition.version!! > 0) {
            movieLiveData.value = condition
            type = condition.type
            version = condition.version
        } else {
            condition.version = null
            movieLiveData.value = condition
            type = condition.type
            version = condition.version
        }
    }

    fun changeVersion(condition: VersionCondition) {
        versionLiveData.value = condition
    }

}