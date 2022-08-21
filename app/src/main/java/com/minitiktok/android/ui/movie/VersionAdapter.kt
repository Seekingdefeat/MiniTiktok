package com.minitiktok.android.ui.movie

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.minitiktok.android.R
import com.minitiktok.android.logic.model.Version

class VersionAdapter(
    private val viewModel: MovieViewModel,
    private val activity: Activity,
    private val versionList: List<Version>
) :
    RecyclerView.Adapter<VersionAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val version: TextView = view.findViewById(R.id.version)
        val activeTime: TextView = view.findViewById(R.id.active_time)
        val startTime: TextView = view.findViewById(R.id.start_time)
        val endTime: TextView = view.findViewById(R.id.end_time)
        val versionCard: MaterialCardView = view.findViewById(R.id.version_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.version_item, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.versionCard.setOnClickListener {
            val position = viewHolder.adapterPosition
            val version = versionList[position]
            viewModel.changeMovie(MovieViewModel.MovieCondition(version.type, version.version))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val version = versionList[position]
        val versionName = when (version.type) {
            1 -> "电影榜"
            2 -> "影视榜"
            3 -> "综艺榜"
            else -> ""
        }
        holder.version.text = "${version.active_time.split(" ")[0]} $versionName"
        holder.activeTime.text = version.active_time
        holder.startTime.text = version.start_time
        holder.endTime.text = version.end_time
    }

    override fun getItemCount() = versionList.size
}