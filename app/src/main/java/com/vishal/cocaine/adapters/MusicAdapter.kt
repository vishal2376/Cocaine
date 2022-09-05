package com.vishal.cocaine.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishal.cocaine.PlayerActivity
import com.vishal.cocaine.R
import com.vishal.cocaine.models.Song
import com.vishal.cocaine.models.formatDuration
import kotlinx.android.synthetic.main.music_item.view.*

class MusicAdapter(var context: Context, private var songList: ArrayList<Song>) :
    RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.music_item, parent, false)
        return MusicViewHolder(view)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        holder.songTitle.text = songList[position].title
        holder.songArtist.text = songList[position].artist
        holder.songDuration.text = formatDuration(songList[position].duration)

        //commented due to lag while scrolling
        //set image
//         setImgArt(context,songList[position].path,holder.imgSong)

        holder.itemView.setOnClickListener {
            val i = Intent(context, PlayerActivity::class.java)
            i.putExtra("INDEX", position)
            i.putExtra("CLASS", "MusicAdapter")
            context.startActivity(i)
        }
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //        val songImg = itemView.imgSong
        val songTitle = itemView.tvSongTitle
        val songArtist = itemView.tvSongArtist
        val songDuration = itemView.tvSongDuration
    }

}
