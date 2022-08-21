package com.minitiktok.android.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.minitiktok.android.databinding.ActivityMainBinding
import com.minitiktok.android.ui.movie.MovieActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        view = binding.root
        setContentView(view)
        binding.button.setOnClickListener {
            MovieActivity.actionStart(this, 1, null)
        }
        binding.button2.setOnClickListener {
            MovieActivity.actionStart(this, 2, null)
        }
        binding.button3.setOnClickListener {
            MovieActivity.actionStart(this, 3, null)
        }
    }
}