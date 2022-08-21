package com.minitiktok.android.ui.movie

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.minitiktok.android.R
import com.minitiktok.android.logic.model.MovieEntity
import com.minitiktok.android.utils.funs.movieDecode
import com.minitiktok.android.utils.funs.sendToast

class MovieAdapter(private val activity: Activity, private val movieList: List<MovieEntity>) :
    RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val moviePoster: ImageView = view.findViewById(R.id.movie_poster)
        val movieName: TextView = view.findViewById(R.id.movie_name)
        val movieActors: TextView = view.findViewById(R.id.movie_actors)
        val movieDirectors: TextView = view.findViewById(R.id.movie_directors)
        val movieReleaseDate: TextView = view.findViewById(R.id.movie_release_date)
        val movieHot: TextView = view.findViewById(R.id.movie_hot)
        val buyBtn: Button = view.findViewById(R.id.film_buy_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movieList[position]
        holder.movieName.text = "${position + 1}.${movie.name}"
        val actors = movie.actors?.movieDecode(" ")
        holder.movieActors.text = "${actors?.get(0)} ${actors?.get(1)} ${actors?.get(2)}"
        holder.movieDirectors.text = movie.directors
        holder.movieReleaseDate.text = movie.releaseDate
        holder.movieHot.text = "${movie.hot?.div(10000)} 万"
        Glide.with(activity).load(movie.poster).placeholder(R.mipmap.ic_launcher)
            .into(holder.moviePoster)
        if (movie.type == 1) holder.buyBtn.visibility = Button.VISIBLE
    }

    override fun getItemCount() = movieList.size

}