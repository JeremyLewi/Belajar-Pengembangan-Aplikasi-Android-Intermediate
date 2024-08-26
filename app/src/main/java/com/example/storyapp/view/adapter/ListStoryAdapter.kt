package com.example.storyapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.databinding.ItemRowStoryBinding

class ListStoryAdapter(private val listStory: List<Story>) :
    RecyclerView.Adapter<ListStoryAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val story = listStory[position]
        Glide.with(holder.itemView.context)
            .load(story.photoUrl)
            .into(holder.binding.ivItemPhoto)

        holder.binding.tvItemName.text = story.name

        holder.binding.tvItemDescription.text = story.description

        holder.itemView.setOnClickListener {

            onItemClickCallback.onItemClicked(listStory[holder.adapterPosition])

        }


    }

    override fun getItemCount(): Int {
        return listStory.size
    }

    class ListViewHolder(var binding: ItemRowStoryBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnItemClickCallback {
        fun onItemClicked(data: Story)
    }

}