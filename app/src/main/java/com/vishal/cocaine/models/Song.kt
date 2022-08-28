package com.vishal.cocaine.models

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val duration: Int = 0,
    val path: String
)
