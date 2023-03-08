package com.cookandroid.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.databinding.ActivityThemePlaylistBinding

class ThemePlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityThemePlaylistBinding
    private lateinit var adapter: MusicAdapter
    private var currentThemePos: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThemePlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentThemePos = intent.extras?.get("index") as Int

        binding.backBtnTP.setOnClickListener { finish() }

        binding.themePlaylistRV.setItemViewCacheSize(10)
        binding.themePlaylistRV.setHasFixedSize(true)
        binding.themePlaylistRV.layoutManager = LinearLayoutManager(this)

        adapter = MusicAdapter(this, MainActivity.allThemePlaylist[currentThemePos].musicList)
        binding.themePlaylistRV.adapter = adapter

    }

    override fun onResume(){
        super.onResume()
        binding.playlistNameTP.text = MainActivity.allThemePlaylist[currentThemePos].name
        Glide.with(this)
            .load(MainActivity.allThemePlaylist[currentThemePos].artUri)
            .apply(RequestOptions().placeholder(R.drawable.spring).centerCrop())
            .into(binding.playlistImgPD)
    }
}