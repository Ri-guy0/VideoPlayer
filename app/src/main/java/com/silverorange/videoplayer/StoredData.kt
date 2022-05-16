package com.silverorange.videoplayer

/*
 * stores the async data to be override with the logic of the video player
 */
interface StoredData {
    fun storedVideos(videoList: MutableList<Video>)
}