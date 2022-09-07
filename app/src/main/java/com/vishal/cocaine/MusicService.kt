package com.vishal.cocaine

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.vishal.cocaine.PlayerActivity.Companion.fabPlayPausePA
import com.vishal.cocaine.PlayerActivity.Companion.isPlaying
import com.vishal.cocaine.PlayerActivity.Companion.musicService
import com.vishal.cocaine.PlayerActivity.Companion.songListPA
import com.vishal.cocaine.PlayerActivity.Companion.songPosition
import com.vishal.cocaine.fragments.NowPlayingFragment
import com.vishal.cocaine.models.getImgArt

class MusicService : Service(), AudioManager.OnAudioFocusChangeListener {

    private var musicBinder = MusicBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    lateinit var audioManager: AudioManager

    override fun onBind(intent: Intent?): IBinder {

        //creating a unique media session for app
        mediaSession = MediaSessionCompat(baseContext, "CocaineSession")

        return musicBinder
    }

    inner class MusicBinder : Binder() {

        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun showNotification(playPauseBtn: Int) {

        //on click intent
        val intent = Intent(baseContext, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this, 0, intent, 0)


        //action intents

        val prevIntent = Intent(
            baseContext,
            NotificationReceiver::class.java
        ).setAction(ApplicationClass.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            500,
            prevIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val playIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            600,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val nextIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            700,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val exitIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            800,
            exitIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        //notification image
        val imgArt = getImgArt(songListPA[songPosition].path)
        val img = if (imgArt != null)
            BitmapFactory.decodeByteArray(imgArt, 0, imgArt.size)
        else
            BitmapFactory.decodeResource(resources, R.drawable.logo)


        //building notification
        val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentIntent(contentIntent)
            .setContentTitle(songListPA[songPosition].title)
            .setContentText(songListPA[songPosition].artist)
            .setSmallIcon(R.drawable.ic_baseline_music)
            .setLargeIcon(img)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_skip_previous, "Previous", prevPendingIntent)
            .addAction(playPauseBtn, "Play", playPendingIntent)
            .addAction(R.drawable.ic_skip_next, "Next", nextPendingIntent)
            .addAction(R.drawable.ic_exit, "Exit", exitPendingIntent)
            .build()

        startForeground(300, notification)

    }

    //on recieving calls and messages
    override fun onAudioFocusChange(focusChange: Int) {
        if (focusChange <= 0) {
            //pause music
            NowPlayingFragment.binding.imgPlayPauseNP.setImageResource(R.drawable.ic_play)
            fabPlayPausePA.setImageResource(R.drawable.ic_play)
            musicService!!.showNotification(R.drawable.ic_play)
            isPlaying = false
            musicService!!.mediaPlayer!!.pause()
        } else {
            //play music
            NowPlayingFragment.binding.imgPlayPauseNP.setImageResource(R.drawable.ic_pause)
            fabPlayPausePA.setImageResource(R.drawable.ic_pause)
            musicService!!.showNotification(R.drawable.ic_pause)
            isPlaying = true
            musicService!!.mediaPlayer!!.start()
        }
    }

}