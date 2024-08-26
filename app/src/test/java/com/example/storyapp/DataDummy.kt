package com.example.storyapp

import com.example.storyapp.data.remote.response.Story
import java.time.LocalDateTime


object DataDummy {

    fun generateDummyStoryResponse(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                photoUrl = "https://picsum.photos/200/300?random=$i",
                createdAt = LocalDateTime.now().toString(),
                name = "Story $i",
                description = "Description for Story $i",
                lon = 0.0,
                id = i.toString(),
                lat = 0.0
            )
            items.add(story)
        }
        return items
    }
}