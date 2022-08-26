package com.vishal.cocaine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import com.vishal.cocaine.fragments.FavoriteFragment
import com.vishal.cocaine.fragments.HomeFragment
import com.vishal.cocaine.fragments.MusicFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val musicFragment = MusicFragment()
    private val favoriteFragment = FavoriteFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadFragment(homeFragment)

        bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.itemHome -> {
                    loadFragment(homeFragment)

                }
                R.id.itemMusic -> loadFragment(musicFragment)
                R.id.itemFavorite -> loadFragment(favoriteFragment)
            }
            true
        }


    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainContainer,fragment)
        transaction.commit()
    }
}