package com.cookandroid.myapplication

import android.os.*
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.myapplication.databinding.ActivityPlayMusicBinding

class PlayMusicActivity : AppCompatActivity() {

    private lateinit var binding:ActivityPlayMusicBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val playList = ArrayList<Int>().apply {
            this.add(1000)
            this.add(1001)
        }
        MusicServiceConnection.musicService!!.setPlayList(playList)

        MusicServiceConnection.musicService!!.setPlayerView(binding.exoControlView)

        //뒤로 가기 버튼
        binding.backBtnPA.setOnClickListener { finish() }


        binding.songNamePA.setOnClickListener {
            setLayout()
        }
    }

    private fun setLayout() {
        binding.songNamePA.text = MusicServiceConnection.musicService!!.getTitle()
    }
}