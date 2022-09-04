package com.vishal.cocaine

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.vishal.cocaine.fragments.MusicFragment
import com.vishal.cocaine.models.Song
import com.vishal.cocaine.models.formatDuration
import com.vishal.cocaine.models.getImgArt
import kotlinx.android.synthetic.main.activity_player.*

class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object {
        lateinit var songListPA: ArrayList<Song>
        var songPosition: Int = 0
        var isPlaying: Boolean = false
        var musicService: MusicService? = null

        lateinit var runnable: Runnable
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        //start music service
        val i = Intent(this, MusicService::class.java)
        bindService(i, this, BIND_AUTO_CREATE)
        startService(i)

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

//        seekbar
        seekBarPA.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser)
                    musicService!!.mediaPlayer!!.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

        })

        //back button
        imgBack.setOnClickListener {
            val back = Intent(this, MainActivity::class.java)
            startActivity(back)
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
        musicService!!.mediaPlayer!!.pause()

        //moving title
        tvSongTitlePA.isSelected = false
    }

    private fun playSong() {
        fabPlayPause.setImageResource(R.drawable.ic_pause)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()

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

            }

            "MusicFragment" -> {
                songListPA = ArrayList()
                songListPA.addAll(MusicFragment.songListMF)
                songListPA.shuffle()

                setLayout()

            }
        }
    }

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

            // on song complete
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)

        } catch (e: Exception) {
            Toast.makeText(this, "Failed to create Media Player", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setLayout() {
        tvSongTitlePA.text = songListPA[songPosition].title
        tvSongArtistPA.text = songListPA[songPosition].artist

        //set image

        //get album art
        val imgArt = getImgArt(songListPA[songPosition].path)
        val img = if (imgArt != null)
            BitmapFactory.decodeByteArray(imgArt, 0, imgArt.size)
        else
            BitmapFactory.decodeResource(resources, R.drawable.logo)

        //load img
        Glide.with(this)
            .load(img)
            .centerCrop()
//            .apply(RequestOptions().placeholder(R.drawable.logo))
            .into(imgCurrentSongPA)
    }

    override fun onCompletion(mediaPlayer: MediaPlayer?) {
        //run next song after song complete
        changeSong(nextSong = true)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MusicBinder
        musicService = binder.currentService()
        createMediaPlayer()
        setSeekBar()

        //show notification
        musicService!!.showNotification()

    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }
}
