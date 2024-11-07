package com.muhammadfahreza.storyapp.view.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.muhammadfahreza.storyapp.R

class StoryAdapter(private val storyList: List<Story>) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    inner class StoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val storyImage: ImageView = view.findViewById(R.id.storyImage)
        val storyTitle: TextView = view.findViewById(R.id.storyTitle)
        val storyDescription: TextView = view.findViewById(R.id.storyDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = storyList[position]
        holder.storyTitle.text = story.title
        holder.storyDescription.text = story.description
        holder.storyImage.setImageResource(story.imageResId) // Jika Anda menggunakan gambar lokal

        // Jika menggunakan URL untuk gambar, gunakan library seperti Glide atau Picasso
        // Glide.with(holder.itemView.context).load(story.imageUrl).into(holder.storyImage)
    }

    override fun getItemCount(): Int = storyList.size
}
