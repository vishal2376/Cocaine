package com.vishal.cocaine.models

data class Playlist(
    val title:String,
    val list: ArrayList<Song>,
    val totalSongs: Int
)
