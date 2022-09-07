package com.vishal.cocaine.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.vishal.cocaine.R
import com.vishal.cocaine.adapters.MusicAdapter
import com.vishal.cocaine.models.Song
import kotlinx.android.synthetic.main.fragment_favorite.*

class FavoriteFragment : Fragment() {

    companion object {
        var songListFF: ArrayList<Song> = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //recycler setup
        setSongRecycler()

    }

    private fun setSongRecycler() {

        rvSongsListFF.layoutManager = LinearLayoutManager(requireContext())
        rvSongsListFF.adapter = MusicAdapter(requireContext(), songListFF)

        val songListAnim = LayoutAnimationController(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.slide_up_anim
            )
        )
        songListAnim.delay = 0.2f
        songListAnim.order = LayoutAnimationController.ORDER_NORMAL
        rvSongsListFF.layoutAnimation = songListAnim

    }

}