package com.minitiktok.android.logic.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.minitiktok.android.utils.funs.movieFormat

data class MovieResp(
    @SerializedName("data") val respDate: MovieRespData,
    @SerializedName("extra") val extraRespData: ExtraRespData
)

data class VersionResp(
    @SerializedName("data") val respDate: VersionRespData,
    @SerializedName("extra") val extraRespData: ExtraRespData
)

data class Movie(
    val actors: List<String>?,
    val areas: List<String>?,
    val directors: List<String>?,
    @SerializedName("discussion_hot") val discussionHot: Long?,
    val hot: Long?,
    @SerializedName("id") val movie_id: String?,
    @SerializedName("influence_hot") val influenceHot: Long?,
    @SerializedName("maoyan_id") val maoyanId: String?,
    val name: String?,
    @SerializedName("name_en") val englishNam: String?,
    val poster: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("search_hot") val searchHot: Long?,
    val tags: List<String>?,
    @SerializedName("topic_hot") val topicHot: Long?,
    val type: Int?
)

@Entity
data class Version(
    val active_time: String,
    val end_time: String,
    val start_time: String,
    val type: Int,
    val version: Int,
    var createTime: Long = 0,
    var cursor: Long = 0,
    var count: Long = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

}

@Entity
data class MovieEntity(
    val actors: String?,
    val areas: String?,
    val directors: String?,
    @SerializedName("discussion_hot") val discussionHot: Long?,
    val hot: Long?,
    @SerializedName("id") val movie_id: String?,
    @SerializedName("influence_hot") val influenceHot: Long?,
    @SerializedName("maoyan_id") val maoyanId: String?,
    val name: String?,
    @SerializedName("name_en") val englishNam: String?,
    val poster: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("search_hot") val searchHot: Long?,
    val tags: String?,
    @SerializedName("topic_hot") val topicHot: Long?,
    val type: Int?,
    var activityTime: String? = null,
    var version: Int? = null
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

data class MovieRespData(
    @SerializedName("active_time") val activeTime: String?,
    val description: String?,
    @SerializedName("error_code") val errorCode: Long?,
    @SerializedName("list") val movies: List<Movie>?
)


data class VersionRespData(
    @SerializedName("list") val versions: List<Version>?,
    val cursor: Long?,
    val description: String,
    val error_code: Long,
    val has_more: Boolean?
)

data class ExtraRespData(
    val description: String?,
    @SerializedName("error_code") val errorCode: Long?,
    val logid: String,
    val now: Long,
    @SerializedName("sub_description") val subDescription: String?,
    @SerializedName("sub_error_code") val subErrorCode: Long?
)


fun Movie.toEntity(activityTime: String? = null, version: Int? = null): MovieEntity {
    val areas = this.areas?.movieFormat(" ")
    val directors = this.directors?.movieFormat(" ")
    val tags = this.tags?.movieFormat(" ")
    val actors = this.actors?.movieFormat(" ")
    return MovieEntity(
        actors,
        areas,
        directors,
        this.discussionHot,
        this.hot,
        this.movie_id,
        this.influenceHot,
        this.maoyanId,
        this.name,
        this.englishNam,
        this.poster,
        this.releaseDate,
        this.searchHot,
        tags,
        this.topicHot,
        this.type,
        activityTime,
        version
    )
}
