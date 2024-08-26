package com.example.storyapp.view.detail

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.databinding.ActivityDetailBinding
import com.example.storyapp.view.main.MainActivity

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val story = intent.getParcelableExtra<Story>(EXTRA_STORY)

        binding.tvDetailName.text = story?.name
        binding.tvDetailDescription.text = story?.description
        Glide.with(this).load(story?.photoUrl).into(binding.ivDetailPhoto)

        startProgress()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

    private fun startProgress() {
        handler = Handler()
        runnable = Runnable {
            val progress = binding.progress1.progress + 1
            binding.progress1.progress = progress
            if (progress > binding.progress1.max) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                handler.postDelayed(runnable, 50) // 100ms for example
            }
        }
        handler.post(runnable)
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}
