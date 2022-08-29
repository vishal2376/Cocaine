package com.vishal.cocaine.adapters

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder

class MusicService : Service() {

    private var musicBinder = MusicBinder()
    var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder {
        return musicBinder
    }

   inner class MusicBinder : Binder() {

        fun currentService():MusicService{
            return this@MusicService
        }
    }
}