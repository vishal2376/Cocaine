package com.vishal.cocaine.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishal.cocaine.R
import com.vishal.cocaine.fragments.PlaylistFragment
import com.vishal.cocaine.models.Playlist
import kotlinx.android.synthetic.main.playlist_item.view.*

class PlaylistAdapter(var context: Context,var playlistData: ArrayList<Playlist>) :
    RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.playlist_item, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.playlistTitle.text = playlistData[position].title
        holder.playlistTitle.isSelected = true
    }

    override fun getItemCount(): Int {
        return playlistData.size
    }

    fun refreshPlaylist(){
        playlistData = ArrayList()
        playlistData.addAll(PlaylistFragment.playlistPF.ref)
        notifyDataSetChanged()
    }

    class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val playlistThumbnail = itemView.imgThumbnailPI
        val playlistTitle = itemView.tvPlaylistTitlePI
        val playlistTotalSong = itemView.tvSongCountPI
    }
}
