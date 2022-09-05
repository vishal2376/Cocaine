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
import com.vishal.cocaine.models.setImgArt
import kotlinx.android.synthetic.main.music_item.view.*

class FavoriteAdapter(var context: Context, private var songList: ArrayList<Song>) :
    RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.music_item, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.songTitle.text = songList[position].title
        holder.songArtist.text = songList[position].artist
        holder.songDuration.text = formatDuration(songList[position].duration)

        //set image
         setImgArt(context,songList[position].path,holder.songImg)

        holder.itemView.setOnClickListener {
            when {
                songList[position].id == PlayerActivity.nowPlayingID -> {
                    sendIntent("NowPlaying", PlayerActivity.songPosition)
                }
                else -> sendIntent("FavoriteAdapter", position)
            }

        }
    }

    private fun sendIntent(ref: String, pos: Int) {
        val i = Intent(context, PlayerActivity::class.java)
        i.putExtra("INDEX", pos)
        i.putExtra("CLASS", ref)
        context.startActivity(i)
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songImg = itemView.imgSong
        val songTitle = itemView.tvSongTitle
        val songArtist = itemView.tvSongArtist
        val songDuration = itemView.tvSongDuration
    }

}
