package com.vishal.cocaine.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vishal.cocaine.PlayerActivity
import com.vishal.cocaine.PlayerActivity.Companion.isPlaying
import com.vishal.cocaine.PlayerActivity.Companion.musicService
import com.vishal.cocaine.PlayerActivity.Companion.nowPlayingID
import com.vishal.cocaine.PlayerActivity.Companion.runnable
import com.vishal.cocaine.PlayerActivity.Companion.songListPA
import com.vishal.cocaine.PlayerActivity.Companion.songPosition
import com.vishal.cocaine.R
import com.vishal.cocaine.databinding.FragmentNowPlayingBinding
import com.vishal.cocaine.models.setImgArt
import com.vishal.cocaine.models.setSongPosition
import kotlinx.android.synthetic.main.fragment_now_playing.*

class NowPlayingFragment : Fragment() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentNowPlayingBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding = FragmentNowPlayingBinding.bind(view)
        binding.root.visibility = View.GONE

        //----------------All Buttons-------------------

        //click on now playing layout
        binding.root.setOnClickListener {
            val i = Intent(context, PlayerActivity::class.java)
            i.putExtra("INDEX", songPosition)
            i.putExtra("CLASS", "NowPlaying")
            startActivity(i)
        }

        //play pause button
        binding.imgPlayPauseNP.setOnClickListener {
            if (isPlaying)
                pauseSong()
            else
                playSong()
        }

        //next button
        binding.imgNextNP.setOnClickListener {
            changeSong(nextSong = true)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        if (musicService != null) {
            binding.root.visibility = View.VISIBLE

            setLayoutNP()
        }

    }

    // set layout of Now Playing
    private fun setLayoutNP() {

        //song image
        setImgArt(
            requireContext(), songListPA[songPosition].path,
            binding.imgCurrentSongNP
        )

        //song title
        binding.tvSongTitleNP.text =
            songListPA[songPosition].title

        //moving title
        tvSongTitleNP.isSelected = true

        //set play pause icon
        if (isPlaying)
            binding.imgPlayPauseNP.setImageResource(R.drawable.ic_pause)
        else
            binding.imgPlayPauseNP.setImageResource(R.drawable.ic_play)

        //set seekbar
        seekBarNP.progress = 0
        seekBarNP.max = musicService!!.mediaPlayer!!.duration
        setSeekBarNP()

    }

      //set seekbar position
    private fun setSeekBarNP() {

        runnable = Runnable {

            seekBarNP.progress = musicService!!.mediaPlayer!!.currentPosition

            //increment seekbar with song position
            Handler(Looper.getMainLooper()).postDelayed(runnable, 1000)
        }

        // start runnable after 0 millisecond
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }

    //--------------song related functions-------------------
    //play song
    private fun playSong() {
        binding.imgPlayPauseNP.setImageResource(R.drawable.ic_pause)
        musicService!!.showNotification(R.drawable.ic_pause)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()

        //moving title
        binding.tvSongTitleNP.isSelected = true
    }

    //pause song
    private fun pauseSong() {
        binding.imgPlayPauseNP.setImageResource(R.drawable.ic_play)
        musicService!!.showNotification(R.drawable.ic_play)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()

        //moving title
        binding.tvSongTitleNP.isSelected = false
    }

    //change song
    private fun changeSong(nextSong: Boolean) {
        setSongPosition(nextSong)

        //setup music player
        if (musicService!!.mediaPlayer == null)
            musicService!!.mediaPlayer = MediaPlayer()

        musicService!!.mediaPlayer!!.reset()
        musicService!!.mediaPlayer!!.setDataSource(songListPA[songPosition].path)
        musicService!!.mediaPlayer!!.prepare()

        //set song id
        nowPlayingID = songListPA[songPosition].id

        //set layout
        setLayoutNP()

        //play song
        playSong()
    }
}
