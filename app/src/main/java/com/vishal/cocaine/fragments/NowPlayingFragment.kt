package com.vishal.cocaine.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vishal.cocaine.PlayerActivity
import com.vishal.cocaine.R
import com.vishal.cocaine.databinding.FragmentNowPlayingBinding
import com.vishal.cocaine.models.setImgArt
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
        return view
    }

    override fun onResume() {
        super.onResume()
        if (PlayerActivity.musicService != null) {
            binding.root.visibility = View.VISIBLE

            setLayoutNP()
        }

    }

    // set layout of Now Playing
    private fun setLayoutNP() {

        //song image
        setImgArt(
            requireContext(), PlayerActivity.songListPA[PlayerActivity.songPosition].path,
            binding.imgCurrentSongNP
        )

        //song title
        binding.tvSongTitleNP.text =
            PlayerActivity.songListPA[PlayerActivity.songPosition].title

        //moving title
        tvSongTitleNP.isSelected = true

        //set play pause icon
        if (PlayerActivity.isPlaying)
            binding.imgPlayPauseNP.setImageResource(R.drawable.ic_pause)
        else
            binding.imgPlayPauseNP.setImageResource(R.drawable.ic_play)

    }
}
