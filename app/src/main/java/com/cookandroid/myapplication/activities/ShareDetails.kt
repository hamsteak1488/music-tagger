package com.cookandroid.myapplication.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.PlaylistManager
import com.cookandroid.myapplication.R
import com.cookandroid.myapplication.databinding.ActivityShareDetailsBinding

class ShareDetails : AppCompatActivity() {

    private lateinit var binding: ActivityShareDetailsBinding
    private var sharingPlaylistPos: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharingPlaylistPos = intent.extras!!.getInt("index",-1)

        binding.titleSD.text = PlaylistManager.playlists[sharingPlaylistPos].name
        binding.uploadBtnSD.setOnClickListener{
//            TODO(공유 리스트 서버에 전송, ShareActivity로 이동)
            val description = binding.editDescriptionSD.text.toString()
            startActivity(Intent(this@ShareDetails, ShareActivity::class.java))
        }
        binding.cancelBtnSD.setOnClickListener { finish() }

    }

    override fun onResume() {
        super.onResume()
        Glide.with(this)
            .load("http://10.0.2.2:8080/img?id=" + (PlaylistManager.playlists[sharingPlaylistPos].musicList[0]))
            .apply(RequestOptions().placeholder(R.drawable.ic_baseline_music_video_24).centerCrop())
            .into(binding.playlistImgSD)
    }
}