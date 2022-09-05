package com.vishal.cocaine.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vishal.cocaine.PlayerActivity.Companion.isPlaying
import com.vishal.cocaine.PlayerActivity.Companion.musicService
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
        musicService!!.mediaPlayer!!.reset()
        musicService!!.mediaPlayer!!.setDataSource(songListPA[songPosition].path)
        musicService!!.mediaPlayer!!.prepare()

        //set layout
        setLayoutNP()

        //play song
        playSong()
    }
}
