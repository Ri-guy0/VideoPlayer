package com.silverorange.videoplayer

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url


/*
 * this class handles the API information, retrieve and any formatting that is needed. It puts the
 * data of each video into the Video class and saves it in the StoredDate interface
 */
class ApiHandler {
    var baseUrl = "http://10.0.0.204:4000/videos/";
    var videoList: MutableList<Video> = ArrayList()

    fun runAPI(videoData: StoredData){
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VideoService::class.java)

        retrofit.getVideos(baseUrl).enqueue(object : Callback<List<TempVideo>>{
            override fun onResponse(call: Call<List<TempVideo>>, response: Response<List<TempVideo>>){

                for (video in response.body()!!) {
                    var newVideo = Video(video.id, video.title, video.publishedAt, video.author.name, video.hlsURL, video.description)
                    videoList.add(newVideo)
                }

                // stores it in the videoData interface to be override with the video player logic
                videoData.storedVideos(videoList)
            }

            override fun onFailure(call: Call<List<TempVideo>>, t: Throwable) {
                Log.e("Error", t.message.toString())
            }

        })
    }

    // temperary data classes for the API retrieval
    data class TempAuthor(val name: String)
    data class TempVideo(val id: String, val title: String, val publishedAt: String, val author: TempAuthor, val hlsURL: String, val description: String)

    // service for getting the API data
    interface VideoService {
        @GET
        fun getVideos(@Url url: String?): Call<List<TempVideo>>
    }
}