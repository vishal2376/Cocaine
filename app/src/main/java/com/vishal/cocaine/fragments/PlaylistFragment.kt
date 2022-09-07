package com.vishal.cocaine.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.vishal.cocaine.R
import com.vishal.cocaine.adapters.PlaylistAdapter
import com.vishal.cocaine.models.Playlist
import kotlinx.android.synthetic.main.fragment_playlist.*

class PlaylistFragment : Fragment() {

    companion object{
       var playlistPF:ArrayList<Playlist> = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set recycler view
        setRecyclerPF()

    }

    private fun setRecyclerPF() {
        //temp list
        val tempData:Array<String> = arrayOf("Playlist 0","Playlist 1","Playlist 2","Playlist 3","Playlist 1","Playlist 2","Playlist 3","Playlist 4","Playlist 5",)

        rvPlaylistPF.layoutManager = GridLayoutManager(requireContext(),2)
        rvPlaylistPF.adapter = PlaylistAdapter(requireContext(),tempData)
    }

}