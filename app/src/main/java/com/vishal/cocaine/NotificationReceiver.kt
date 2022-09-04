package com.vishal.cocaine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlin.system.exitProcess

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ApplicationClass.PREVIOUS -> {}

            ApplicationClass.PLAY -> {
                if (PlayerActivity.isPlaying)
                    pauseMusic()
                else
                    playMusic()
            }
            ApplicationClass.NEXT -> {}

            ApplicationClass.EXIT -> {
                PlayerActivity.musicService!!.stopForeground(true)
                PlayerActivity.musicService = null
                exitProcess(1)
            }
        }
    }

    private fun playMusic() {
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_pause)
        PlayerActivity.fabPlayPausePA.setImageResource(R.drawable.ic_pause)
    }

    private fun pauseMusic() {
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.stop()
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_play)
        PlayerActivity.fabPlayPausePA.setImageResource(R.drawable.ic_play)
    }

}