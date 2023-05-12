package com.cookandroid.myapplication.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.*
import com.cookandroid.myapplication.MusicServiceConnection.serverUrl
import com.cookandroid.myapplication.databinding.ActivityShareDetailsBinding
import com.tftf.util.PlaylistForShare

class ShareDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShareDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playlistTitleSD.text = PlaylistManager.exploringPlaylist!!.name
        binding.uploadBtnSD.setOnClickListener{
            val playlistForShare = PlaylistForShare(
                UserManager.userID,
                binding.playlistTitleSD.text.toString(),
                binding.editDescriptionSD.toString(),
                PlaylistManager.exploringPlaylist!!.musicIDList,
                0,
                0
            )

            RetrofitManager.uploadSharedList(playlistForShare) { uploadSucceed ->
                if (uploadSucceed == true)
                    Toast.makeText(this@ShareDetailsActivity, "upload completed!", Toast.LENGTH_SHORT).show()
                else {
                    Toast.makeText(this@ShareDetailsActivity, "upload failed!", Toast.LENGTH_SHORT).show()
                }
                finish()
            }
        }
        binding.cancelBtnSD.setOnClickListener { finish() }

        Glide.with(this)
            .load(serverUrl + "img?id=" + (PlaylistManager.exploringPlaylist!!.musicIDList[0]))
            .apply(RequestOptions().placeholder(R.drawable.ic_baseline_music_video_24).centerCrop())
            .into(binding.playlistImgSD)
    }

    override fun onResume() {
        super.onResume()
    }
}