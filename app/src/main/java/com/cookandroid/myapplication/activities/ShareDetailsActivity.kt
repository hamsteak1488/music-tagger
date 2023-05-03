package com.cookandroid.myapplication.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.MusicServiceConnection
import com.cookandroid.myapplication.MusicServiceConnection.serverUrl
import com.cookandroid.myapplication.PlaylistManager
import com.cookandroid.myapplication.R
import com.cookandroid.myapplication.databinding.ActivityShareDetailsBinding
import com.cookandroid.myapplication.PlaylistManager.exploringListPos
import com.tftf.util.PlaylistForShare

class ShareDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShareDetailsBinding
    private val mService = MusicServiceConnection.musicService!!
    private val exploringList = PlaylistManager.playlists[exploringListPos]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playlistTitleSD.text = PlaylistManager.playlists[exploringListPos].name
        binding.uploadBtnSD.setOnClickListener{
            val playlistForShare = PlaylistForShare(
                binding.playlistTitleSD.text.toString(),
                PlaylistManager.playlists[exploringListPos].musicList,
                mService.email,
                binding.editDescriptionSD.text.toString(),
                0,
                0
            )

            mService.uploadShareList(playlistForShare) { uploadSuccessed ->
                if (uploadSuccessed)
                    Toast.makeText(this@ShareDetailsActivity, "upload completed!", Toast.LENGTH_SHORT).show()
                else {
                    Toast.makeText(this@ShareDetailsActivity, "upload failed!", Toast.LENGTH_SHORT).show()
                }
            }

            val description = binding.editDescriptionSD.text.toString()
            finish()
        }
        binding.cancelBtnSD.setOnClickListener { finish() }

        Glide.with(this)
            .load(serverUrl + "img?id=" + (PlaylistManager.playlists[exploringListPos].musicList[0]))
            .apply(RequestOptions().placeholder(R.drawable.ic_baseline_music_video_24).centerCrop())
            .into(binding.playlistImgSD)
    }

    override fun onResume() {
        super.onResume()
    }
}