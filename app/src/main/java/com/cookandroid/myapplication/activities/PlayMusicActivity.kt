package com.cookandroid.myapplication.activities

import android.app.Service
import android.content.res.AssetManager
import android.os.*
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.MusicService
import com.cookandroid.myapplication.MusicServiceConnection
import com.cookandroid.myapplication.MusicServiceConnection.serverUrl
import com.cookandroid.myapplication.PlaylistManager
import com.cookandroid.myapplication.R
import com.cookandroid.myapplication.databinding.ActivityPlayMusicBinding

class PlayMusicActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayMusicBinding
    private val mService = MusicServiceConnection.musicService!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtnPA.setOnClickListener { finish() }

        mService.setViewPlayer(binding.exoControlView)

        if (mService.currentListPos != -1 && mService.currentMusicPos != -1) {
            setLayout()
            mService.setMediaItemChangeListenerForPlayMusicActivity(object:MusicService.OnMediaItemChangeListener {
                override fun onMediaItemChange() {
                    setLayout()
                }
            })
            mService.prepareAndPlay()
        }
    }

    override fun onResume() {
        super.onResume()


    }

    override fun onDestroy() {
        super.onDestroy()

        mService.removeMediaItemChangeListenerForPlayMusicActivity()
    }

    private fun setLayout() {
        mService.getMusicMetadata(PlaylistManager.playlists[mService.currentListPos].musicList[mService.currentMusicPos]) {
            if (it == null) return@getMusicMetadata
            binding.songNamePA.text = it.title
            binding.songNamePA.invalidate()

            Glide.with(this@PlayMusicActivity)
                .load(serverUrl + "img?id=" + (it.id))
                .apply(RequestOptions().placeholder(R.drawable.ic_baseline_music_note_24).centerCrop())
                .into(binding.songImg)
            binding.songImg.invalidate()
        }
    }
}