package com.vishal.cocaine.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.vishal.cocaine.R
import com.vishal.cocaine.adapters.MusicAdapter
import com.vishal.cocaine.models.Song
import kotlinx.android.synthetic.main.fragment_music.*
import java.io.File


class MusicFragment : Fragment() {

    companion object{
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

        //load all songs
        songListMF = loadAllSongs()


        //recycler setup
        rvSongsList.layoutManager = LinearLayoutManager(requireContext())
        rvSongsList.adapter = MusicAdapter(requireContext(), songListMF)
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
            MediaStore.Audio.Media.DATA
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
                        cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))

                    val song = Song(
                        id = idC,
                        title = titleC,
                        artist = artistC,
                        duration = durationC,
                        path = pathC
                    )

                    // add data if file exits
                    val file = File(song.path)
                    if(file.exists())
                        tempSongList.add(song)

                } while (cursor.moveToNext())
                cursor.close()
            }

        return tempSongList
    }

}