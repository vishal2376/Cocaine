package com.vishal.cocaine.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vishal.cocaine.R
import com.vishal.cocaine.adapters.PlaylistAdapter
import com.vishal.cocaine.databinding.FragmentPlaylistBinding
import com.vishal.cocaine.models.Playlist

class PlaylistFragment : Fragment() {

    companion object {
        var playlistPF: ArrayList<Playlist> = ArrayList()

        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentPlaylistBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_playlist, container, false)
        binding = FragmentPlaylistBinding.bind(view)

        //set recycler view
        setRecyclerPF()

        //add playlist button
        binding.fabAddPlaylistPF.setOnClickListener {
            addPlaylistDialog()
        }

        return view
    }

    private fun addPlaylistDialog() {
        val addDialog =
            LayoutInflater.from(requireContext())
                .inflate(R.layout.playlist_dialog, binding.root, false)
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(addDialog)
            .setTitle("New Playlist")
            .setPositiveButton("ADD") { dialog, _ ->
            dialog.dismiss()
        }.show()

    }

    private fun setRecyclerPF() {
        //temp list
        val tempData: Array<String> = arrayOf(
            "Playlist 0",
            "Playlist 1",
            "Playlist 2",
            "Playlist 3",
            "Playlist 1",
            "Playlist 2",
            "Playlist 3",
            "Playlist 4",
            "Playlist 5",
        )

        binding.rvPlaylistPF.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvPlaylistPF.adapter = PlaylistAdapter(requireContext(), tempData)
    }

}