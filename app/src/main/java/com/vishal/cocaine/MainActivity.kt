package com.vishal.cocaine

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.vishal.cocaine.fragments.FavoriteFragment
import com.vishal.cocaine.fragments.HomeFragment
import com.vishal.cocaine.fragments.MusicFragment
import com.vishal.cocaine.fragments.PlaylistFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val favoriteFragment = FavoriteFragment()
    private val musicFragment = MusicFragment()
    private val playlistFragment = PlaylistFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //request storage permission
        requestRuntimePermissions()

        //load music fragment by default
        loadFragment(musicFragment)


        // bottom nav bar
        bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.itemHome -> loadFragment(homeFragment)
                R.id.itemMusic -> loadFragment(musicFragment)
                R.id.itemFavorite -> loadFragment(favoriteFragment)
                R.id.itemPlaylist -> loadFragment(playlistFragment)
            }
            true
        }


    }


    private fun requestRuntimePermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                100
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                100
            )
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainContainer, fragment)
        transaction.commit()
    }
}