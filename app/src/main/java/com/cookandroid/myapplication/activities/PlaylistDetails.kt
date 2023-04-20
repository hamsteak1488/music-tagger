package com.cookandroid.myapplication.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.*
import com.cookandroid.myapplication.databinding.ActivityPlaylistDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder



class PlaylistDetails : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistDetailsBinding
    private lateinit var adapter: MusicAdapter
    private var currentPlaylistPos: Int = -1

    private lateinit var mService:MusicService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mService = MusicServiceConnection.musicService!!

        currentPlaylistPos = intent.extras!!.getInt("index", -1)

        binding.playlistNamePD.text = PlaylistManager.playlists[currentPlaylistPos].name

        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this)

        //음악 추가
        binding.addBtnPD.setOnClickListener{
            val addMusicIntent = Intent(this, SearchActivity::class.java)
            addMusicIntent.putExtra("searchForAdd", true)
            addMusicIntent.putExtra("listPos", currentPlaylistPos)
            startActivity(addMusicIntent)
        }
        //음악 전체 삭제
        binding.removeAllPD.setOnClickListener{
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("remove")
                .setMessage("Remove all songs from playlist?")
                .setPositiveButton("Yes"){dialog, _ ->
                    PlaylistManager.playlists[currentPlaylistPos].musicList.clear()
                    mService.savePlaylistManager(mService.email){ }
                    dialog.dismiss()
                    finish()
                    startActivity(intent)
                }
                .setNegativeButton("No"){dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()

            //setDialogBtnBackground(this, customDialog)
        }
    }

    override fun onResume() {
        super.onResume()

        binding.moreInfoPD.text = "Total ${PlaylistManager.playlists[currentPlaylistPos].musicList.size} Songs.\n\n"
        if(PlaylistManager.playlists[currentPlaylistPos].musicList.size > 0){
            mService.getMusicMetadataList(PlaylistManager.playlists[currentPlaylistPos].musicList) {
                if (it == null) return@getMusicMetadataList
                adapter = MusicAdapter(this@PlaylistDetails, it.toCollection(ArrayList()),
                    object: MusicAdapter.OnItemClickListener {
                        override fun onItemClick(view: View, pos: Int) {
                            mService.currentListPos = currentPlaylistPos
                            mService.setMusicPos(pos)
                            mService.reloadPlayer()
                            startActivity(Intent(this@PlaylistDetails, PlayMusicActivity::class.java))
                        }
                    })
                binding.playlistDetailsRV.adapter = adapter
            }
            Glide.with(this)
                .load("http://10.0.2.2:8080/img?id=" + (PlaylistManager.playlists[currentPlaylistPos].musicList[0]))
                .apply(RequestOptions().placeholder(R.drawable.ic_baseline_music_video_24).centerCrop())
                .into(binding.playlistImgPD)
        }
    }
}