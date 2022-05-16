package com.silverorange.videoplayer

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.format.DateTimeFormatter
import java.util.*

class Video(private var id: String,
            private var title: String,
            private var publishedAt: String,
            private var  author: String,
            private var link: String,
            private var desc: String) {
    fun getId(): String {
        return id
    }

    fun getTitle(): String {
        return title
    }

    fun getPublishedAt(): String {
        return publishedAt
    }

    fun getAuthor(): String {
        return author
    }

    fun getLink(): String {
        return link
    }

    fun getDescription(): String {
        return desc
    }
}