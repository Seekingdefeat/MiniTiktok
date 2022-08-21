package com.minitiktok.android.ui.movie

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.minitiktok.android.R
import com.minitiktok.android.databinding.ActivityMovieRuleBinding

class MovieRuleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMovieRuleBinding

    companion object {
        fun actionStart(context: Context, type: Int = 1) {
            val intent = Intent(context, MovieRuleActivity::class.java).apply {
                putExtra("type", type)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieRuleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when (intent.getIntExtra("type", 1)) {
            1 -> {
                binding.ruleLayout.rule2Tv.text = this.getString(R.string.rule2_direct_film)
                binding.ruleLayout.rule3Tv.text = this.getString(R.string.rule3_direct_film)
            }
            2->{
                binding.ruleLayout.rule2Tv.text = this.getString(R.string.rule2_direct_tv)
                binding.ruleLayout.rule3Tv.text = this.getString(R.string.rule3_direct_tv)
            }
            3->{
                binding.ruleLayout.rule2Tv.text = this.getString(R.string.rule2_direct_avar)
                binding.ruleLayout.rule3Tv.text = this.getString(R.string.rule3_direct_avar)
            }
        }

        binding.ruleLayout.ruleBackButton.setOnClickListener {
            finish()
        }
    }
}