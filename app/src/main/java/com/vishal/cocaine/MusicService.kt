package com.vishal.cocaine

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat

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

        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun showNotification(){

        //action intents

        val prevIntent = Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext,500,prevIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent = Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext,600,playIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext,700,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val exitIntent = Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext,800,exitIntent,PendingIntent.FLAG_UPDATE_CURRENT)


        //building notification
        val notification = NotificationCompat.Builder(baseContext,ApplicationClass.CHANNEL_ID)
            .setContentTitle(PlayerActivity.songListPA[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.songListPA[PlayerActivity.songPosition].artist)
            .setSmallIcon(R.drawable.ic_baseline_music)
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.logo))
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_skip_previous,"Previous",prevPendingIntent)
            .addAction(R.drawable.ic_pause,"Play",playPendingIntent)
            .addAction(R.drawable.ic_skip_next,"Next",nextPendingIntent)
            .addAction(R.drawable.ic_exit,"Exit",exitPendingIntent)
            .build()

        startForeground(300,notification)

    }

}