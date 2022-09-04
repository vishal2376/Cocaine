package com.vishal.cocaine

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vishal.cocaine.fragments.MusicFragment
import com.vishal.cocaine.models.Song
import com.vishal.cocaine.models.formatDuration
import com.vishal.cocaine.models.setImgArt
import com.vishal.cocaine.models.setSongPosition
import kotlinx.android.synthetic.main.activity_player.*

class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object {
        var musicService: MusicService? = null
        lateinit var runnable: Runnable

        //song data
        lateinit var songListPA: ArrayList<Song>
        var songPosition: Int = 0

        //player behaviour
        var isPlaying: Boolean = false

        //layout elements
        lateinit var fabPlayPausePA: FloatingActionButton

        @SuppressLint("StaticFieldLeak")
        lateinit var tvTitlePA: TextView

        @SuppressLint("StaticFieldLeak")
        lateinit var tvArtistPA: TextView

        @SuppressLint("StaticFieldLeak")
        lateinit var imgSongPA: ImageView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        //start music service
        val i = Intent(this, MusicService::class.java)
        bindService(i, this, BIND_AUTO_CREATE)
        startService(i)

        //init layout elements,songListPA array and set layout
        initialize()

        //---------------------------All Buttons-------------------------
        //play pause button
        fabPlayPause.setOnClickListener {
            if (isPlaying)
                pauseSong()
            else
                playSong()
        }

        //prev button
        imgPrevious.setOnClickListener {
            changeSong(nextSong = false)
        }

        //next button
        imgNext.setOnClickListener {
            changeSong(nextSong = true)
        }

        //back button
        imgBack.setOnClickListener {
            val back = Intent(this, MainActivity::class.java)
            startActivity(back)
        }

        //-------------seekbar change handler------------
        seekBarPA.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser)
                    musicService!!.mediaPlayer!!.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

        })

    }

    //---------------------------All functions----------------------------

    //initialize
    private fun initialize() {


        //init layout elements
        fabPlayPausePA = fabPlayPause
        tvTitlePA = tvSongTitlePA
        tvArtistPA = tvSongArtistPA
        imgSongPA = imgCurrentSongPA

        // init song related var
        songPosition = intent.getIntExtra("INDEX", 0)

        // init songList according to class
        when (intent.getStringExtra("CLASS")) {
            "MusicAdapter" -> {
                songListPA = ArrayList()
                songListPA.addAll(MusicFragment.songListMF)

                setLayout()
            }

            "MusicFragment" -> {
                songListPA = ArrayList()
                songListPA.addAll(MusicFragment.songListMF)
                songListPA.shuffle()

                setLayout()
            }
        }
    }

    //-------------- Song related functions------------------
    //change song
    private fun changeSong(nextSong: Boolean) {
        setSongPosition(nextSong)

        // init music player again
        setLayout()
        createMediaPlayer()
    }

    //play song
    private fun playSong() {
        fabPlayPause.setImageResource(R.drawable.ic_pause)
        musicService!!.showNotification(R.drawable.ic_pause)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()

        //moving title
        tvSongTitlePA.isSelected = true
    }

    //pause song
    private fun pauseSong() {
        fabPlayPause.setImageResource(R.drawable.ic_play)
        musicService!!.showNotification(R.drawable.ic_play)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()

        //moving title
        tvSongTitlePA.isSelected = false
    }

    //------------player setup related functions-------------

    //create music player
    private fun createMediaPlayer() {
        try {
            if (musicService!!.mediaPlayer == null)
                musicService!!.mediaPlayer = MediaPlayer()

            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(songListPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()

            isPlaying = true
            fabPlayPause.setImageResource(R.drawable.ic_pause)

            //seek bar
            tvSeekBarEnd.text =
                formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            tvSeekBarEnd.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())

            seekBarPA.progress = 0
            seekBarPA.max = musicService!!.mediaPlayer!!.duration

            //show notification
            musicService!!.showNotification(R.drawable.ic_pause)

            //call 'on song complete' function
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)

        } catch (e: Exception) {
            Toast.makeText(this, "Failed to create Media Player", Toast.LENGTH_SHORT).show()
        }
    }

    //set layout of music player
    private fun setLayout() {
        //set song info
        tvSongTitlePA.text = songListPA[songPosition].title
        tvSongArtistPA.text = songListPA[songPosition].artist

        //moving title
        tvSongTitlePA.isSelected = true

        //set image
        setImgArt(this, songListPA[songPosition].path, imgCurrentSongPA)

    }

    //set seekbar position
    private fun setSeekBar() {

        runnable = Runnable {
            tvSeekBarStart.text =
                formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            seekBarPA.progress = musicService!!.mediaPlayer!!.currentPosition

            //increment seekbar with song position
            Handler(Looper.getMainLooper()).postDelayed(runnable, 1000)
        }

        // start runnable after 0 millisecond
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }

    //----------------on song completion----------------
    override fun onCompletion(mediaPlayer: MediaPlayer?) {
        //run next song after song complete
        changeSong(nextSong = true)
    }

    //---------------- Music Service functions---------------
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MusicBinder
        musicService = binder.currentService()
        createMediaPlayer()
        setSeekBar()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }
}
