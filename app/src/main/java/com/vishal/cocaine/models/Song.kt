package com.vishal.cocaine.models

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vishal.cocaine.PlayerActivity
import com.vishal.cocaine.PlayerActivity.Companion.songListPA
import com.vishal.cocaine.PlayerActivity.Companion.songPosition
import com.vishal.cocaine.R
import com.vishal.cocaine.fragments.FavoriteFragment
import java.util.concurrent.TimeUnit

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val duration: Long = 0,
    val path: String,
    val artUri: String
)

fun formatDuration(duration: Long): String {
    val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS)
            - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))

    return String.format("%02d:%02d", minutes, seconds)
}

fun getImgArt(path: String?): ByteArray? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}

fun setImgArt(context: Context, path: String?, img: ImageView) {

    //get album art
    val imgArt = getImgArt(path)
    val imgBitmap = if (imgArt != null)
        BitmapFactory.decodeByteArray(imgArt, 0, imgArt.size)
    else
        BitmapFactory.decodeResource(context.resources, R.drawable.logo)

    //load img
    Glide.with(context)
        .load(imgBitmap)
        .centerCrop()
        .apply(RequestOptions().placeholder(R.drawable.logo))
        .into(img)
}


fun setSongPosition(nextSong: Boolean) {

    if (!PlayerActivity.isRepeat) {
        if (nextSong) {
            //checking song position
            if (songListPA.size - 1 == songPosition)
                songPosition = 0
            else
                songPosition++

        } else {
            //checking song position
            if (songPosition == 0)
                songPosition = songListPA.size - 1
            else
                songPosition--
        }
    }
}

fun checkFavorite(id: String): Int {
    PlayerActivity.isFavorite = false
    FavoriteFragment.songListFF.forEachIndexed { index, song ->
        if (id == song.id) {
            PlayerActivity.isFavorite = true
            return index
        }
    }
    return -1
}