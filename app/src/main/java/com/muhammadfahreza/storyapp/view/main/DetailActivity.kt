package com.muhammadfahreza.storyapp.view.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.muhammadfahreza.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra(EXTRA_TITLE)
        val description = intent.getStringExtra(EXTRA_DESCRIPTION)
        val imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL)

        binding.tvDetailTitle.text = title
        binding.tvDetailDesc.text = description
        Glide.with(this)
            .load(imageUrl)
            .into(binding.ivDetail)
    }

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_DESCRIPTION = "extra_description"
        const val EXTRA_IMAGE_URL = "extra_image_url"
    }
}
