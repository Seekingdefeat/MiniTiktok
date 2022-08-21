package com.minitiktok.android.ui.movie

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.minitiktok.android.databinding.ActivityMovieBinding
import com.minitiktok.android.logic.model.Version
import com.minitiktok.android.utils.funs.getThisWeekHour
import com.minitiktok.android.utils.funs.logUtils
import com.minitiktok.android.utils.funs.sendToast
import com.scwang.smart.refresh.footer.ClassicsFooter
import java.text.SimpleDateFormat
import java.util.*

class MovieActivity : AppCompatActivity() {
    val viewModel by lazy { ViewModelProvider(this).get(MovieViewModel::class.java) }
    private lateinit var binding: ActivityMovieBinding
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var versionAdapter: VersionAdapter

    companion object {
        fun actionStart(context: Context, type: Int, version: Int? = null) {
            val intent = Intent(context, MovieActivity::class.java).apply {
                putExtra("type", type)
                putExtra("version", version)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //设置recycleView
        val layoutManager = LinearLayoutManager(this)
        binding.bottomLayout.videoRecyclerView.layoutManager = layoutManager
        movieAdapter = MovieAdapter(this, viewModel.movieList)
        binding.bottomLayout.videoRecyclerView.adapter = movieAdapter

        val layoutManager2 = LinearLayoutManager(this)
        versionAdapter = VersionAdapter(this.viewModel, this, viewModel.versionList)
        binding.versionRecycleView.adapter = versionAdapter
        binding.versionRecycleView.layoutManager = layoutManager2


        //刷新页面
        val type = intent.getIntExtra("type", 2)
        val version = intent.getIntExtra("version", -1)
        if (version > 0) {
            viewModel.changeMovie(MovieViewModel.MovieCondition(type, version))
        } else {
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            viewModel.versionList.add(
                Version(
                    "本周",
                    sdf.format(Date().getThisWeekHour(6, 12, TimeZone.getTimeZone("GMT+08"))),
                    sdf.format(Date().getThisWeekHour(0, 12, TimeZone.getTimeZone("GMT+08"))),
                    type,
                    -1
                )
            )
            viewModel.changeMovie(MovieViewModel.MovieCondition(type))
        }
        viewModel.changeVersion(MovieViewModel.VersionCondition(type))

        //设置刷新图标
        val smartRefreshLayout = binding.refreshVersion
        smartRefreshLayout.setRefreshFooter(ClassicsFooter(this))
        smartRefreshLayout.setOnLoadMoreListener {
            //获得当前最后一个数据
            val lastVersion = viewModel.versionList[viewModel.versionList.size - 1]
            //刷新操作
            viewModel.changeVersion(MovieViewModel.VersionCondition(lastVersion.type, lastVersion))
        }

        //设置按钮或ui
        when (type) {
            1 -> binding.topLayout.titleText.text = "电影排行榜"
            2 -> binding.topLayout.titleText.text = "影视排行榜"
            3 -> binding.topLayout.titleText.text = "综艺排行榜"
        }
        binding.topLayout.backButton.setOnClickListener {
            finish()
        }
        binding.bottomLayout.listRule.setOnClickListener {
            MovieRuleActivity.actionStart(this, type)
        }
        binding.topLayout.forwardButton.setOnClickListener {
            binding.versionDrawerLayout.openDrawer(GravityCompat.END)
        }

        //监听数据
        viewModel.moviesLiveData.observe(this) {
            val result = it.getOrNull()
            if (result != null) {
                viewModel.movieList.clear()
                viewModel.movieList.addAll(result)
                viewModel.activityTime.value = result[0].activityTime.toString()
                movieAdapter.notifyDataSetChanged()
            } else {
                logUtils.d("榜单", "获取电影失败")
                "获取电影失败".sendToast()
            }
            binding.versionDrawerLayout.closeDrawer(GravityCompat.END)
        }
        viewModel.versionsLiveData.observe(this) {
            val result = it.getOrNull()
            if (result != null) {
                viewModel.versionList.addAll(result)
                versionAdapter.notifyDataSetChanged()
            } else {
                logUtils.d("版本", "获取版本失败")
                "获取版本失败".sendToast()
            }
            //结束刷新
            smartRefreshLayout.finishLoadMore()
        }
        viewModel.activityTime.observe(this) {
            runOnUiThread {
                binding.bottomLayout.updateTimeMessage.text = it
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}