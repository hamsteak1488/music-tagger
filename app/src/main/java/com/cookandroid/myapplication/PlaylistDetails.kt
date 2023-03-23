package com.cookandroid.myapplication

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
    private lateinit var adapter: MusicAdapter

    private var currentPlaylistPos: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentPlaylistPos = intent.extras!!.getInt("index", -1)

        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this)

        adapter = MusicAdapter(this@PlaylistDetails, PlaylistManager.allPlayList[currentPlaylistPos].musicList,
            object:MusicAdapter.OnItemClickListener {
                override fun onItemClick(view: View, pos: Int) {
                    MusicServiceConnection.musicService!!.currentListPos = currentPlaylistPos
                    MusicServiceConnection.musicService!!.setMusicPos(pos)
                    MusicServiceConnection.musicService!!.reloadPlayer()
                    startActivity(Intent(this@PlaylistDetails, PlayMusicActivity::class.java))
                }
            })

        binding.playlistDetailsRV.adapter = adapter

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
                    PlaylistManager.allPlayList[currentPlaylistPos].musicList.clear()
                    //adapter.refreshPlaylist()
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
        binding.moreInfoPD.text = "Total ${PlaylistManager.allPlayList[currentPlaylistPos].musicList.size} Songs.\n\n"
        if(PlaylistManager.allPlayList[currentPlaylistPos].musicList.size > 0){
            Glide.with(this)
                .load("http://10.0.2.2:8080/img?id=" + (PlaylistManager.allPlayList[currentPlaylistPos].musicList[0].id))
                .apply(RequestOptions().placeholder(R.drawable.ic_baseline_music_video_24).centerCrop())
                .into(binding.playlistImgPD)
        }
    }
}