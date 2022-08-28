package com.vishal.cocaine

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vishal.cocaine.fragments.MusicFragment
import com.vishal.cocaine.models.Song

class PlayerActivity : AppCompatActivity() {

    companion object {
        lateinit var songListPA: ArrayList<Song>
        var songPosition: Int = 0
        var mediaPlayer : MediaPlayer ?= null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        when (intent.getStringExtra("CLASS")) {
            "MusicAdapter" -> {
                songListPA = ArrayList()
                songListPA.addAll(MusicFragment.songListMF)

                if(mediaPlayer == null)
                    mediaPlayer = MediaPlayer()

                mediaPlayer!!.reset()
                mediaPlayer!!.setDataSource(songListPA[songPosition].path)
                mediaPlayer!!.prepare()
                mediaPlayer!!.start()

            }
        }


    }
}