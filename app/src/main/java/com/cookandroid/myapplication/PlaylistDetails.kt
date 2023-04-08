package com.cookandroid.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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

        adapter = MusicAdapter(this@PlaylistDetails, PlaylistManager.allPlayList[currentPlaylistPos].musicList)

        binding.playlistDetailsRV.adapter = adapter

        //뒤로 가기
        binding.backBtnPD.setOnClickListener { finish() }

        //음악 추가
        binding.addBtnPD.setOnClickListener{
            val addMusicIntent = Intent(this, SearchActivity::class.java)
            addMusicIntent.putExtra("searchForAdd", true)
            addMusicStartForResult.launch(addMusicIntent)
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

    val addMusicStartForResult:ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val musicID = it.data!!.getIntExtra("musicID", -1)
                MusicServiceConnection.musicService!!.getMusicMetadata(musicID) {
                    PlaylistManager.allPlayList[currentPlaylistPos].musicList.add(it!!)
                }
            }
    }

    // todo: 음악을 검색해서 추가한 후, 뒤로가기 눌렀을 때 곧바로 앨범이미지와 곡 개수 정보들이 화면에 반영되지 않음
    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        binding.playlistNamePD.text = PlaylistManager.allPlayList[currentPlaylistPos].name
        binding.moreInfoPD.text = "Total ${adapter.itemCount} Songs.\n\n"
        if(adapter.itemCount > 0){
            Glide.with(this)
                .load("http://10.0.2.2:8080/img?id=" + (PlaylistManager.allPlayList[currentPlaylistPos].musicList[0].id))
                .apply(RequestOptions().placeholder(R.drawable.ic_baseline_music_video_24).centerCrop())
                .into(binding.playlistImgPD)
        }
        adapter.notifyDataSetChanged()
    }
}