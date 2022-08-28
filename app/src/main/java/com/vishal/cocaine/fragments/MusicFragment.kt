package com.vishal.cocaine.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.vishal.cocaine.R
import com.vishal.cocaine.adapters.MusicAdapter
import kotlinx.android.synthetic.main.fragment_music.*


class MusicFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //testing data
        val tempData = arrayOf("Song 1","Song 2","Song 3","Song 4","Song 5","Song 6","Song 7","Song 8","Song 9","Song 10")

        //recycler setup
        rvSongsList.layoutManager = LinearLayoutManager(requireContext())
        rvSongsList.adapter = MusicAdapter(requireContext(),tempData)
    }

}