package com.silverorange.videoplayer

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import java.util.*


class MainActivity : AppCompatActivity(), Player.Listener {
    // initializes the variables
    lateinit var player: ExoPlayer
    lateinit var playerView: PlayerView
    lateinit var playPauseButton: ImageView
    lateinit var nextButton: ImageView
    lateinit var previousButton: ImageView
    lateinit var detailTextbox: TextView
    lateinit var titleTextbox: TextView
    // initial conditions of the position in the list, the length and if its playing
    var counter = 0
    var length = 0
    var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // computer ip address: 10.0.0.204:400/videos
        initializeExoPlayer()
        val apiHandler = ApiHandler()
        loadApi(apiHandler)
    }

    /*
     * handles the asynchronous state of the api to work with the loading of the sychronous page
     * stores the asynchronous date and overrides it with the video players functionality in the
     * stored videos function
     */
    fun loadApi(apiHandler: ApiHandler) {
        apiHandler.runAPI(object: StoredData {
            override fun storedVideos(videoList: MutableList<Video>) {
                loadPage(videoList)
            }
        })
    }

    /*
     * does the initilization of the video player page, loads the first video and contains
     * the click listeners for the different buttons
     */
    fun loadPage(videoList: MutableList<Video>){
        //sorts using a custom comparision for the video class
        val videoCompare = VideoCompare()
        Collections.sort(videoList, videoCompare)
        length=videoList.size

        // loads the first video
        loadVideo(videoList[counter])

        playPauseButton = findViewById<ImageView>(R.id.playPause)
        nextButton = findViewById(R.id.nextButton)
        previousButton = findViewById(R.id.previousButton)
        detailTextbox = findViewById(R.id.details)

        detailTextbox.movementMethod = ScrollingMovementMethod(); //adds scrollability

        // toggles the play button
        playPauseButton.setOnClickListener{
            togglePlay()
        }

        // checks if its the last video, if not it changes to the next video
        nextButton.setOnClickListener{
            if (counter<(length-1)) {
                counter++
                loadVideo(videoList[counter])
            }
        }

        // checks if its the first video, if not it changes to the next video
        previousButton.setOnClickListener{
            if (counter>0) {
                counter--
                loadVideo(videoList[counter])
            }
        }
    }

    /*
     * checks if it is already playing or not and flips the toggle
     */
    fun togglePlay(){
        playPauseButton = findViewById<ImageView>(R.id.playPause)
        if (isPlaying) {
            player.pause()
            playPauseButton.setImageResource(R.drawable.ic_play);
        }
        else {
            player.play()
            playPauseButton.setImageResource(R.drawable.ic_pause);
        }
        isPlaying=!isPlaying
    }

    /*
     * custom comparision for the list of video objects by publish date
     */
    class VideoCompare : Comparator<Video> {
        override fun compare(videoOne: Video, videoTwo: Video): Int {
            return videoOne.getPublishedAt().compareTo(videoTwo.getPublishedAt())
        }
    }

    /*
     * initializes the explayer and listener. applies the player to the view
     */
    fun initializeExoPlayer() {
        player = ExoPlayer.Builder(this).build()
        playerView = findViewById(R.id.videoPlayer)
        playerView.player = player;
        player.addListener(this)
    }

    /*
     * the loading of the video and the description of the video
     */
    fun loadVideo (video: Video) {
        detailTextbox = findViewById(R.id.details)
        titleTextbox=findViewById(R.id.videoTitle)
        playPauseButton = findViewById<ImageView>(R.id.playPause)

        // formats the String.xml content with the video information. using teh String.xml to make it
        // clear as well as allowing for translations
        var formatedString = getString(R.string.description_template,
            video.getAuthor(), video.getPublishedAt(), video.getDescription())

        titleTextbox.text = video.getTitle()
        detailTextbox.text = formatedString

        // gets the mdeia item from the url of the videp
        val mediaItem: MediaItem = MediaItem.fromUri(video.getLink())
        player.setMediaItem(mediaItem)
        player.prepare()

        // resets the player to being paused even when switching videos.
        player.pause()
        isPlaying=false
        playPauseButton.setImageResource(R.drawable.ic_play);
    }
}