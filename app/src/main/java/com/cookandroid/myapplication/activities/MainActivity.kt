package com.cookandroid.myapplication.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.myapplication.*
import com.cookandroid.myapplication.ControlViewManager.displayControlView
import com.cookandroid.myapplication.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.tftf.util.Music
import com.tftf.util.Playlist

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    private var themeAdapter: ThemeViewAdapter? = null
    private lateinit var topRankAdapter: MusicAdapter

    private val mService = MusicServiceConnection.musicService!!

    companion object {
        var instance: MainActivity? = null
        fun applicationContext(): Context { return instance!!.applicationContext}
    }

    init{
        instance = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mService.loadData()

        ///랜덤, 플레이리스트, 검색 버튼
        binding.recommendBtn.setOnClickListener{
            SurroundingsManager.getCurrentSurroundings { surroundings ->
                val mService = MusicServiceConnection.musicService!!
                mService.getPersonalizedList(surroundings, 20) { musicList ->
                    if (musicList == null) return@getPersonalizedList
                    PlaylistManager.playlists[0] = Playlist("tempPlaylist",
                        musicList as ArrayList<Int>
                    )
                    mService.reloadPlayer(0, 0)
                    startActivity(Intent(this@MainActivity, PlayMusicActivity::class.java))
                }
            }
        }
        binding.playlistBtn.setOnClickListener{
            startActivity(Intent(this@MainActivity, ListOfPlaylistActivity::class.java).apply {
                putExtra("operation", ActivityOperation.LIST_OF_PLAYLIST_EXPLORE.ordinal)
            }) }
        binding.searchSongBtn.setOnClickListener {
            startActivity((Intent(this@MainActivity, SearchActivity::class.java).apply {
                putExtra("operation", ActivityOperation.SEARCH_EXPLORE.ordinal)
            }))
        }

        //로그아웃 버튼
        auth = FirebaseAuth.getInstance()
        binding.logoutBtn.setOnClickListener{
            auth.signOut()

            //로그아웃시 자동로그인 안되게 수정
            val pref: SharedPreferences = getSharedPreferences("pref", Activity.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = pref.edit()
            editor.putBoolean("auto",false)
            editor.putBoolean("ox",false)
            editor.apply()
            Intent(this, LoginActivity::class.java).also{
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
                Toast.makeText(this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show()
            }
        }


        //메인 RV 구현
        // mainPlaylist = getMainList()

        binding.mainRV.setItemViewCacheSize(5)
        binding.mainRV.setHasFixedSize(true)
        binding.mainRV.layoutManager = LinearLayoutManager(this@MainActivity)

        //topRankAdapter = MusicAdapter(this, mainPlaylist)
        //binding.mainRV.adapter = adapterMain
    }

    override fun onResume() {
        super.onResume()

        displayControlView(binding.exoControlView)

        //테마 RV 구현
        SurroundingsManager.getCurrentSurroundings { surroundings ->
            MusicServiceConnection.musicService!!.getThemeList(surroundings, 10) { themeLists ->
                binding.themeListRV.setItemViewCacheSize(5)
                binding.themeListRV.setHasFixedSize(true)
                binding.themeListRV.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)


                if (themeAdapter == null) return@getThemeList

                themeAdapter = ThemeViewAdapter(this, themeLists!!.toCollection(ArrayList()),
                    object : ThemeViewAdapter.OnItemClickListener {
                        override fun onItemClick(view: View, pos: Int) {
                            PlaylistManager.playlists[0] = themeLists[pos]
                            PlaylistManager.exploringListPos = 0

                            startActivity(Intent(this@MainActivity, PlaylistDetailsActivity::class.java))
                        }

                    })
                binding.themeListRV.adapter = themeAdapter
            }
        }
    }

    private fun getMainList():ArrayList<Music>{
        return ArrayList<Music>()
    }


}