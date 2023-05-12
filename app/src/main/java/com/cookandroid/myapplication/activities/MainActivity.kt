package com.cookandroid.myapplication.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.myapplication.*
import com.cookandroid.myapplication.ControlViewManager.displayControlView
import com.cookandroid.myapplication.adapters.ThemeViewAdapter
import com.cookandroid.myapplication.adapters.TopRankAdapter
import com.cookandroid.myapplication.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.tftf.util.Playlist

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    private var themeAdapter: ThemeViewAdapter? = null
    private var topRankAdapter: TopRankAdapter? = null

    private val mService = MusicServiceConnection.musicService!!

    private val topRankSize = 10

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

        binding.topRankTV.text = "이번 달 TOP " + topRankSize

        ///랜덤, 플레이리스트, 검색 버튼
        binding.personalRecommendBtn.setOnClickListener{
            SurroundingsManager.getCurrentSurroundings { surroundings ->
                RetrofitManager.getPersonalizedList(UserManager.userID, surroundings, 20) { playlist ->
                    if (playlist == null) return@getPersonalizedList
                    PlaylistManager.exploringPlaylist = playlist
                    startActivity(Intent(this@MainActivity, PlaylistDetailsActivity::class.java))
                }
            }
        }
        binding.generalRecommendBtn.setOnClickListener{
            SurroundingsManager.getCurrentSurroundings { surroundings ->
                RetrofitManager.getGeneralizedList(surroundings, 20) { playlist ->
                    if (playlist == null) return@getGeneralizedList
                    PlaylistManager.exploringPlaylist = playlist
                    startActivity(Intent(this@MainActivity, PlaylistDetailsActivity::class.java))
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
        binding.shareBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, ShareActivity::class.java))
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
    }

    override fun onResume() {
        super.onResume()

        displayControlView(binding.exoControlView)

        //테마 RV 구현
        SurroundingsManager.getCurrentSurroundings { surroundings ->
            RetrofitManager.getThemeList(surroundings, 10) { themeLists ->

                if (themeLists == null || themeLists.isEmpty()) return@getThemeList

                binding.themeListRV.setItemViewCacheSize(5)
                binding.themeListRV.setHasFixedSize(true)
                binding.themeListRV.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)

                themeAdapter = ThemeViewAdapter(this, themeLists.toCollection(ArrayList()),
                    object : ThemeViewAdapter.OnItemClickListener {
                        override fun onItemClick(view: View, pos: Int) {
                            PlaylistManager.exploringPlaylist = themeLists[pos]
                            startActivity(Intent(this@MainActivity, PlaylistDetailsActivity::class.java))
                        }

                    })
                binding.themeListRV.adapter = themeAdapter
            }
        }

        RetrofitManager.getTopRankList(topRankSize) { topRankList ->

            if (topRankList == null || topRankList.musicIDList.isEmpty()) return@getTopRankList

            RetrofitManager.getMusicMetadataList(topRankList.musicIDList) { musicList ->

                binding.topRankRV.setItemViewCacheSize(5)
                binding.topRankRV.setHasFixedSize(true)
                binding.topRankRV.layoutManager = LinearLayoutManager(this@MainActivity)

                topRankAdapter = TopRankAdapter(this@MainActivity, musicList!!.toCollection(ArrayList()), null,
                    object : TopRankAdapter.OnItemClickListener {
                        override fun onItemClick(view: View, pos: Int) {
                            PlaylistManager.exploringPlaylist = topRankList
                            startActivity(Intent(this@MainActivity, PlaylistDetailsActivity::class.java))
                        }

                    })
                binding.topRankRV.adapter = topRankAdapter
            }
        }
    }
}