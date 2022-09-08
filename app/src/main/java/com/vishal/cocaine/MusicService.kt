package com.vishal.cocaine

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
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

        // headphone button control
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val playbackSpeed = if (isPlaying) 1F else 0F
            mediaSession.setMetadata(
                MediaMetadataCompat.Builder()
                    .putLong(
                        MediaMetadataCompat.METADATA_KEY_DURATION,
                        mediaPlayer!!.duration.toLong()
                    )
                    .build()
            )
            val playBackState = PlaybackStateCompat.Builder()
                .setState(
                    PlaybackStateCompat.STATE_PLAYING,
                    mediaPlayer!!.currentPosition.toLong(),
                    playbackSpeed
                )
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                .build()
            mediaSession.setPlaybackState(playBackState)
            mediaSession.setCallback(object : MediaSessionCompat.Callback() {

                //play and pause onclick event
                override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
                    if (isPlaying) {
                        //pause music
                        fabPlayPausePA.setImageResource(R.drawable.ic_play)
                        NowPlayingFragment.binding.imgPlayPauseNP.setImageResource(R.drawable.ic_play)
                        isPlaying = false
                        mediaPlayer!!.pause()
                        showNotification(R.drawable.ic_play)
                    } else {

                        //play music
                        fabPlayPausePA.setImageResource(R.drawable.ic_pause)
                        NowPlayingFragment.binding.imgPlayPauseNP.setImageResource(R.drawable.ic_pause)
                        isPlaying = true
                        mediaPlayer!!.start()
                        showNotification(R.drawable.ic_pause)
                    }
                    return super.onMediaButtonEvent(mediaButtonEvent)
                }

                override fun onSeekTo(pos: Long) {
                    super.onSeekTo(pos)
                    mediaPlayer!!.seekTo(pos.toInt())
                    val playBackStateNew = PlaybackStateCompat.Builder()
                        .setState(
                            PlaybackStateCompat.STATE_PLAYING,
                            mediaPlayer!!.currentPosition.toLong(),
                            playbackSpeed
                        )
                        .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                        .build()
                    mediaSession.setPlaybackState(playBackStateNew)
                }
            })
        }

        startForeground(300, notification)

    }

    //on receiving calls and messages
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