package com.cookandroid.myapplication

import android.os.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.databinding.ActivityPlayMusicBinding

class PlayMusicActivity : AppCompatActivity() {

    private lateinit var binding:ActivityPlayMusicBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtnPA.setOnClickListener { finish() }

        MusicServiceConnection.musicService!!.setPlayerView(binding.exoControlView)

        if (MusicServiceConnection.musicService!!.currentList != null) {
            setLayout()
        }
    }

    private fun setLayout() {
        binding.songNamePA.text = MusicServiceConnection.musicService!!.currentMusic!!.title
        Glide.with(this@PlayMusicActivity)
            .load("http://10.0.2.2:8080/img?id=" + (MusicServiceConnection.musicService!!.currentMusic!!.id))
            .apply(RequestOptions().placeholder(R.drawable.ic_baseline_music_note_24).centerCrop())
            .into(binding.songImg)
    }
}