package com.muhammadfahreza.storyapp.view.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.muhammadfahreza.storyapp.R
import com.muhammadfahreza.storyapp.data.response.ListStoryItem

class StoryAdapter : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {
    private val storyList = mutableListOf<ListStoryItem>()

    fun submitList(stories: List<ListStoryItem>) {
        storyList.clear()
        storyList.addAll(stories)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = storyList[position]
        holder.bind(story)
    }

    override fun getItemCount(): Int = storyList.size

    class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.storyTitle)
        private val tvDescription: TextView = itemView.findViewById(R.id.storyDescription)
        private val imgStory: ImageView = itemView.findViewById(R.id.storyImage)

        fun bind(story: ListStoryItem) {
            tvName.text = story.name
            tvDescription.text = story.description
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(imgStory)
        }
    }
}
