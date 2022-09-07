package com.vishal.cocaine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.vishal.cocaine.PlayerActivity.Companion.favIndex
import com.vishal.cocaine.PlayerActivity.Companion.imgFavoritePA
import com.vishal.cocaine.PlayerActivity.Companion.imgSongPA
import com.vishal.cocaine.PlayerActivity.Companion.isFavorite
import com.vishal.cocaine.PlayerActivity.Companion.musicService
import com.vishal.cocaine.PlayerActivity.Companion.songListPA
import com.vishal.cocaine.PlayerActivity.Companion.songPosition
import com.vishal.cocaine.PlayerActivity.Companion.tvArtistPA
import com.vishal.cocaine.PlayerActivity.Companion.tvTitlePA
import com.vishal.cocaine.fragments.NowPlayingFragment
import com.vishal.cocaine.models.checkFavorite
import com.vishal.cocaine.models.setImgArt
import com.vishal.cocaine.models.setSongPosition
import kotlin.system.exitProcess

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ApplicationClass.PREVIOUS -> {
                changeSong(nextSong = false, context = context!!)
            }

            ApplicationClass.PLAY -> {
                if (PlayerActivity.isPlaying)
                    pauseMusic()
                else
                    playMusic()
            }
            ApplicationClass.NEXT -> {
                changeSong(nextSong = true, context = context!!)
            }

            ApplicationClass.EXIT -> {
                musicService!!.audioManager.abandonAudioFocus(PlayerActivity.musicService)
                musicService!!.stopForeground(true)
                musicService!!.mediaPlayer!!.release()
                musicService = null
                exitProcess(1)
            }
        }
    }

    private fun playMusic() {
        PlayerActivity.isPlaying = true
        musicService!!.mediaPlayer!!.start()
        musicService!!.showNotification(R.drawable.ic_pause)
        PlayerActivity.fabPlayPausePA.setImageResource(R.drawable.ic_pause)
        NowPlayingFragment.binding.imgPlayPauseNP.setImageResource(R.drawable.ic_pause)
    }

    private fun pauseMusic() {
        PlayerActivity.isPlaying = false
        musicService!!.mediaPlayer!!.pause()
        musicService!!.showNotification(R.drawable.ic_play)
        PlayerActivity.fabPlayPausePA.setImageResource(R.drawable.ic_play)
        NowPlayingFragment.binding.imgPlayPauseNP.setImageResource(R.drawable.ic_play)
    }

    private fun changeSong(nextSong: Boolean, context: Context) {
        setSongPosition(nextSong)

        musicService!!.mediaPlayer!!.reset()
        musicService!!.mediaPlayer!!.setDataSource(songListPA[songPosition].path)
        musicService!!.mediaPlayer!!.prepare()

        tvTitlePA.text = songListPA[songPosition].title
        tvArtistPA.text = songListPA[songPosition].artist

        //set image
        setImgArt(context, songListPA[songPosition].path, imgSongPA)

        //set fav index
        favIndex = checkFavorite(songListPA[songPosition].id)

        //set fav icon
        if (isFavorite)
            imgFavoritePA.setImageResource(R.drawable.ic_baseline_favorite)
        else
            imgFavoritePA.setImageResource(R.drawable.ic_favorite)


        playMusic()
    }


}