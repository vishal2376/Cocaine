package com.vishal.cocaine

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vishal.cocaine.fragments.MusicFragment
import com.vishal.cocaine.models.Song
import com.vishal.cocaine.models.formatDuration
import kotlinx.android.synthetic.main.activity_player.*

class PlayerActivity : AppCompatActivity(), MediaPlayer.OnCompletionListener {

    companion object {
        lateinit var songListPA: ArrayList<Song>
        var songPosition: Int = 0
        var mediaPlayer: MediaPlayer? = null
        var isPlaying: Boolean = false

        lateinit var runnable: Runnable
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        //init songList and set layout
        initialize()

        //moving title
        tvSongTitlePA.isSelected = true

        //play pause button
        fabPlayPause.setOnClickListener {
            if (isPlaying)
                pauseSong()
            else
                playSong()
        }

        //prev and next song button
        imgPrevious.setOnClickListener {
            changeSong(nextSong = false)
        }

        imgNext.setOnClickListener {
            changeSong(nextSong = true)
        }

        //seekbar
        seekBarPA.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser)
                    mediaPlayer!!.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

        })

        //back button
        imgBack.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }

    }

    private fun changeSong(nextSong: Boolean) {
        if (nextSong) {

            //checking song position
            if (songListPA.size - 1 == songPosition)
                songPosition = 0
            else
                songPosition++

        } else {
            //checking song position
            if (songPosition == 0)
                songPosition = songListPA.size - 1
            else
                songPosition--
        }

        // init music player again
        setLayout()
        createMediaPlayer()
    }

    private fun pauseSong() {
        fabPlayPause.setImageResource(R.drawable.ic_play)
        isPlaying = false
        mediaPlayer!!.pause()

        //moving title
        tvSongTitlePA.isSelected = false
    }

    private fun playSong() {
        fabPlayPause.setImageResource(R.drawable.ic_pause)
        isPlaying = true
        mediaPlayer!!.start()

        //moving title
        tvSongTitlePA.isSelected = true
    }

    private fun initialize() {

        songPosition = intent.getIntExtra("INDEX", 0)

        when (intent.getStringExtra("CLASS")) {
            "MusicAdapter" -> {
                songListPA = ArrayList()
                songListPA.addAll(MusicFragment.songListMF)

                setLayout()
                createMediaPlayer()
                setSeekBar()

            }

            "MusicFragment" -> {
                songListPA = ArrayList()
                songListPA.addAll(MusicFragment.songListMF)
                songListPA.shuffle()

                setLayout()
                createMediaPlayer()
                setSeekBar()
            }
        }
    }

    private fun setSeekBar() {

        runnable = Runnable {
            tvSeekBarStart.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            seekBarPA.progress = mediaPlayer!!.currentPosition

            //increment seekbar with song position
            Handler(Looper.getMainLooper()).postDelayed(runnable, 1000)
        }

        // start runnable after 0 millisecond
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }

    private fun createMediaPlayer() {
        try {
            if (mediaPlayer == null)
                mediaPlayer = MediaPlayer()

            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(songListPA[songPosition].path)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()

            isPlaying = true
            fabPlayPause.setImageResource(R.drawable.ic_pause)

            //seek bar
            tvSeekBarEnd.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            tvSeekBarEnd.text = formatDuration(mediaPlayer!!.duration.toLong())

            seekBarPA.progress = 0
            seekBarPA.max = mediaPlayer!!.duration

            // on song complete
            mediaPlayer!!.setOnCompletionListener(this)

        } catch (e: Exception) {
            Toast.makeText(this, "Failed to create Media Player", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setLayout() {
        tvSongTitlePA.text = songListPA[songPosition].title
        tvSongArtistPA.text = songListPA[songPosition].artist
    }

    override fun onCompletion(mediaPlayer: MediaPlayer?) {
        //run next song after song complete
        changeSong(nextSong = true)
    }
}