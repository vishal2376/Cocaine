package com.vishal.cocaine.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.vishal.cocaine.R
import com.vishal.cocaine.adapters.FavoriteAdapter
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
        rvSongsListFF.layoutManager = LinearLayoutManager(requireContext())
        rvSongsListFF.adapter = FavoriteAdapter(requireContext(), songListFF)

    }
}