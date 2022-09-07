package com.vishal.cocaine.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.vishal.cocaine.PlayerActivity
import com.vishal.cocaine.R
import com.vishal.cocaine.adapters.MusicAdapter
import com.vishal.cocaine.models.Song
import kotlinx.android.synthetic.main.fragment_music.*
import java.io.File
import kotlin.system.exitProcess


class MusicFragment : Fragment() {

    companion object {
        lateinit var songListMF: ArrayList<Song>
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initialize
        FavoriteFragment.songListFF = ArrayList()

        //load all songs
        songListMF = loadAllSongs()

        //load fav songs
        loadFavSongs()

        //shuffle button
        fabShuffle.setOnClickListener {
            val i = Intent(requireContext(), PlayerActivity::class.java)
            i.putExtra("INDEX", 0)
            i.putExtra("CLASS", "MusicFragment")
            requireContext().startActivity(i)
        }

        //recycler setup
        setSongRecycler()
    }

    private fun setSongRecycler() {

        rvSongsList.layoutManager = LinearLayoutManager(requireContext())
        rvSongsList.adapter = MusicAdapter(requireContext(), songListMF)

        val songListAnim = LayoutAnimationController(AnimationUtils.loadAnimation(requireContext(),R.anim.slide_up_anim))
        songListAnim.delay = 0.2f
        songListAnim.order = LayoutAnimationController.ORDER_NORMAL
        rvSongsList.layoutAnimation = songListAnim

    }

    private fun loadFavSongs() {
        //load fav list data using data preferences
        val editor = requireContext().getSharedPreferences("FAVORITE", Context.MODE_PRIVATE)
        val jsonString = editor.getString("FavoriteSongs", null)
        val tokenType = object : TypeToken<ArrayList<Song>>() {}.type
        if (jsonString != null) {
            val favData: ArrayList<Song> = GsonBuilder().create().fromJson(jsonString, tokenType)
            FavoriteFragment.songListFF.addAll(favData)
        }
    }

    @SuppressLint("Recycle", "Range")
    private fun loadAllSongs(): ArrayList<Song> {
        val tempSongList = ArrayList<Song>()

        // selecting audio that are not null
        val selection = MediaStore.Audio.Media.TITLE + " != 0"

        // song info we need
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )

        // init cursor
        val cursor = requireContext().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC",
            null
        )

        // extracting data from song using cursor
        if (cursor != null)
            if (cursor.moveToFirst()) {
                do {
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val titleC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val artistC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val durationC =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))

                    // to get song image path
                    val albumID =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                            .toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri, albumID).toString()

                    val song = Song(
                        id = idC,
                        title = titleC,
                        artist = artistC,
                        duration = durationC,
                        path = pathC,
                        artUri = artUriC
                    )

                    // add data if file exits
                    val file = File(song.path)
                    if (file.exists())
                        tempSongList.add(song)

                } while (cursor.moveToNext())
                cursor.close()
            }

        return tempSongList
    }

    override fun onResume() {
        super.onResume()

        //store fav list data using data preferences
        val jsonString = GsonBuilder().create().toJson(FavoriteFragment.songListFF)
        val editor = requireContext().getSharedPreferences("FAVORITE", Context.MODE_PRIVATE).edit()
        editor.putString("FavoriteSongs", jsonString)
        editor.apply()
    }

    @SuppressLint("CommitPrefEdits")
    override fun onDestroy() {
        super.onDestroy()

        if (!PlayerActivity.isPlaying && PlayerActivity.musicService != null) {
            PlayerActivity.musicService!!.stopForeground(true)
            PlayerActivity.musicService!!.mediaPlayer!!.release()
            PlayerActivity.musicService = null
            exitProcess(1)
        }
    }

}