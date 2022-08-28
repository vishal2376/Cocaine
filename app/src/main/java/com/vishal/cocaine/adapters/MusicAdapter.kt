package com.vishal.cocaine.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishal.cocaine.R
import kotlinx.android.synthetic.main.music_item.view.*

class MusicAdapter(var context: Context, private var musicData:Array<String>): RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.music_item,parent,false)
        return MusicViewHolder(view)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        holder.songTitle.text = musicData[position]
    }

    override fun getItemCount(): Int {
        return musicData.size
    }

    class MusicViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val songImg = itemView.imgSong
        val songTitle = itemView.tvSongTitle
        val songArtist = itemView.tvSongArtist
        val songDuration = itemView.tvSongDuration
    }

}
