package com.vishal.cocaine

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vishal.cocaine.fragments.FavoriteFragment
import com.vishal.cocaine.fragments.MusicFragment
import com.vishal.cocaine.models.*
import kotlinx.android.synthetic.main.activity_player.*

class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object {
        var musicService: MusicService? = null
        lateinit var runnable: Runnable

        //song data
        lateinit var songListPA: ArrayList<Song>
        var songPosition: Int = 0
        var nowPlayingID: String = ""

        //player behaviour
        var isPlaying: Boolean = false
        var isRepeat: Boolean = false
        var isFavorite: Boolean = false
        var favIndex: Int = -1

        //layout elements
        lateinit var fabPlayPausePA: FloatingActionButton

        @SuppressLint("StaticFieldLeak")
        lateinit var tvTitlePA: TextView

        @SuppressLint("StaticFieldLeak")
        lateinit var tvArtistPA: TextView

        @SuppressLint("StaticFieldLeak")
        lateinit var imgSongPA: ImageView

        @SuppressLint("StaticFieldLeak")
        lateinit var imgFavoritePA: ImageView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        //init layout elements,songListPA array and set layout
        initialize()

        //---------------------------All Buttons-------------------------
        //equalizer button
        imgEqualizer.setOnClickListener {
            openEqualizer()
        }

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

        //repeat button
        imgRepeat.setOnClickListener {
            if (!isRepeat) {
                isRepeat = true
                imgRepeat.setColorFilter(ContextCompat.getColor(this, R.color.colorAccentDark))
            } else {
                isRepeat = false
                imgRepeat.setColorFilter(ContextCompat.getColor(this, R.color.text))
            }
        }

        //shuffle button
        imgShuffle.setOnClickListener {
            Toast.makeText(this, "Implementing soon", Toast.LENGTH_SHORT).show()
        }

        //back button
        imgBack.setOnClickListener {
            finish()
        }

        //fav button
        imgFavPA.setOnClickListener {
            //set fav icon
            if (isFavorite) {
                isFavorite = false
                imgFavPA.setImageResource(R.drawable.ic_favorite)
                FavoriteFragment.songListFF.removeAt(favIndex)
            } else {
                isFavorite = true
                imgFavPA.setImageResource(R.drawable.ic_baseline_favorite)
                FavoriteFragment.songListFF.add(songListPA[songPosition])
            }

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
        imgFavoritePA = imgFavPA

        // init song related var
        songPosition = intent.getIntExtra("INDEX", 0)

        // init songList according to class
        when (intent.getStringExtra("CLASS")) {
            "MusicAdapter" -> {
                initMusicService()

                songListPA = ArrayList()
                songListPA.addAll(MusicFragment.songListMF)

                setLayout()
            }

            "FavoriteAdapter" -> {
                initMusicService()

                songListPA = ArrayList()
                songListPA.addAll(FavoriteFragment.songListFF)

                setLayout()
            }

            "MusicFragment" -> {
                initMusicService()

                songListPA = ArrayList()
                songListPA.addAll(MusicFragment.songListMF)
                songListPA.shuffle()

                setLayout()
            }

            "NowPlaying" -> {
                setLayout()

                //set play pause icon
                if (isPlaying)
                    fabPlayPausePA.setImageResource(R.drawable.ic_pause)
                else
                    fabPlayPausePA.setImageResource(R.drawable.ic_play)


                //seek bar
                tvSeekBarEnd.text =
                    formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                tvSeekBarEnd.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())

                seekBarPA.progress = musicService!!.mediaPlayer!!.currentPosition
                seekBarPA.max = musicService!!.mediaPlayer!!.duration

                setSeekBar()
            }
        }
    }

    //---------------Start Music Services---------------------
    private fun initMusicService() {
        val i = Intent(this, MusicService::class.java)
        bindService(i, this, BIND_AUTO_CREATE)
        startService(i)
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

    // open equalizer
    private fun openEqualizer() {
        try {
            val equalizerIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
            equalizerIntent.putExtra(
                AudioEffect.EXTRA_AUDIO_SESSION,
                musicService!!.mediaPlayer!!.audioSessionId
            )
            equalizerIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
            equalizerIntent.putExtra(
                AudioEffect.EXTRA_CONTENT_TYPE,
                AudioEffect.CONTENT_TYPE_MUSIC
            )

            startActivityForResult(equalizerIntent, 10)

        } catch (e: Exception) {
            Toast.makeText(this, "Equalizer feature not supported", Toast.LENGTH_SHORT).show()
        }
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

            //set song id
            nowPlayingID = songListPA[songPosition].id

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
        //set fav index
        favIndex = checkFavorite(songListPA[songPosition].id)

        //set song info
        tvSongTitlePA.text = songListPA[songPosition].title
        tvSongArtistPA.text = songListPA[songPosition].artist

        //moving title
        tvSongTitlePA.isSelected = true

        //set image
        setImgArt(this, songListPA[songPosition].path, imgCurrentSongPA)

        //set button colors
        if (isRepeat)
            imgRepeat.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.colorAccentDark
                )
            )

        //set fav icon
        if (isFavorite)
            imgFavPA.setImageResource(R.drawable.ic_baseline_favorite)
        else
            imgFavPA.setImageResource(R.drawable.ic_favorite)


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

    //------------------- Activity Result-------------------
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //checking for equalizer
        if (resultCode == 10 && resultCode == RESULT_OK)
            return

    }
}
