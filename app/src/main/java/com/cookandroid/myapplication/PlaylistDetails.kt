package com.cookandroid.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.databinding.ActivityPlaylistDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder



class PlaylistDetails : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistDetailsBinding
    private lateinit var adapter: Adapter

    companion object{
        var currentPlaylistPos: Int = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentPlaylistPos = intent.extras?.get("index") as Int

        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this)

        //PlaylistManager.allPlayList[currentPlaylistPos].playlist.addAll(AllPlaylist)
        adapter = Adapter(this, PlaylistManager.allPlayList[currentPlaylistPos].playlist, playlistDetails = true)


        binding.playlistDetailsRV.adapter = adapter

        //뒤로 가기
        binding.backBtnPD.setOnClickListener { finish() }

        //음악 추가
        binding.addBtnPD.setOnClickListener{
            startActivity(Intent(this, SearchActivity::class.java))
        }
        //음악 전체 삭제
        binding.removeAllPD.setOnClickListener{
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("remove")
                .setMessage("Remove all songs from playlist?")
                .setPositiveButton("Yes"){dialog, _ ->
                    PlaylistManager.allPlayList[currentPlaylistPos].playlist.clear()
                    adapter.refreshPlaylist()
                    dialog.dismiss()
                }
                .setNegativeButton("No"){dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()

            setDialogBtnBackground(this, customDialog)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.playlistNamePD.text = PlaylistManager.allPlayList[currentPlaylistPos].name
        binding.moreInfoPD.text = "Total ${adapter.itemCount} Songs.\n\n"

        if(adapter.itemCount > 0){
            Glide.with(this)
                .load(PlaylistManager.allPlayList[currentPlaylistPos].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.ic_baseline_music_video_24).centerCrop())
                .into(binding.playlistImgPD)
        }
        adapter.notifyDataSetChanged()
    }
}