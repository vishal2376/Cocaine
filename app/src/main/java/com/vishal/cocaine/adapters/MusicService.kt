package com.vishal.cocaine.adapters

import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.vishal.cocaine.ApplicationClass
import com.vishal.cocaine.PlayerActivity
import com.vishal.cocaine.R

class MusicService : Service() {

    private var musicBinder = MusicBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession : MediaSessionCompat

    override fun onBind(intent: Intent?): IBinder {

        //creating a unique media session for app
        mediaSession = MediaSessionCompat(baseContext,"CocaineSession")

        return musicBinder
    }

   inner class MusicBinder : Binder() {

        fun currentService():MusicService{
            return this@MusicService
        }
    }

    fun showNotification(){
        val notification = NotificationCompat.Builder(baseContext,ApplicationClass.CHANNEL_ID)
            .setContentTitle(PlayerActivity.songListPA[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.songListPA[PlayerActivity.songPosition].artist)
            .setSmallIcon(R.drawable.ic_baseline_music)
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.logo))
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_skip_previous,"Previous",null)
            .addAction(R.drawable.ic_play,"Play",null)
            .addAction(R.drawable.ic_skip_next,"Next",null)
            .addAction(R.drawable.ic_exit,"Exit",null)
            .build()

        startForeground(300,notification)

    }

}