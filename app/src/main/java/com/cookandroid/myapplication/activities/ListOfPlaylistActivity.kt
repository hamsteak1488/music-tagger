package com.cookandroid.myapplication.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.myapplication.*
import com.cookandroid.myapplication.PlaylistManager.exploringListPos
import com.cookandroid.myapplication.adapters.PlaylistViewAdapter
import com.cookandroid.myapplication.databinding.ActivityListOfPlaylistBinding
import com.cookandroid.myapplication.databinding.AddPlaylistBinding
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tftf.util.Playlist
import java.util.*

class ListOfPlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListOfPlaylistBinding
    private lateinit var adapter: PlaylistViewAdapter

    private var operationOrdinal:Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListOfPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        operationOrdinal = intent.getIntExtra("operation", -1)
        binding.ListOfPlaylistTitleTV.text = when (operationOrdinal) {
            ActivityOperation.LIST_OF_PLAYLIST_EXPLORE.ordinal -> "Playlist"
            ActivityOperation.LIST_OF_PLAYLIST_MOVE.ordinal -> "Move Playlist"
            ActivityOperation.LIST_OF_PLAYLIST_SHARE.ordinal -> "Share Playlist"
            else -> "Playlist"
        }
        if (operationOrdinal != ActivityOperation.LIST_OF_PLAYLIST_EXPLORE.ordinal) {
            binding.instructionPA.visibility = View.GONE
            binding.addPlaylistBtn.visibility = View.GONE
        }

        binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(13)
        binding.playlistRV.layoutManager = LinearLayoutManager(this@ListOfPlaylistActivity)
        binding.backBtnPLA.setOnClickListener { finish() }
        binding.addPlaylistBtn.setOnClickListener { customAlertDialog() }

        //생성된 플레이리스트가 존재하면 (플레이리스트 생성 문구) 노출 x
        if(PlaylistManager.playlists.isNotEmpty()) binding.instructionPA.visibility = View.GONE
    }

    //플레이리스트 추가 창
    private fun customAlertDialog(){
        val customDialog = LayoutInflater.from(this@ListOfPlaylistActivity).inflate(R.layout.add_playlist, binding.root, false)
        val binder = AddPlaylistBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(this)
        val dialog = builder.setView(customDialog)
            .setTitle("Playlist Details")
            .setPositiveButton("ADD"){ dialog, _ ->
                val playlistName = binder.playlistName.text
                if(playlistName != null)
                    if(playlistName.isNotEmpty())
                    {
                        addPlaylist(playlistName.toString())
                    }

                dialog.dismiss()
            }
            .setNegativeButton("Cancel"){dialog,_ ->
                dialog.dismiss()
            }.create()

        dialog.show()
        
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)?.setBackgroundColor(
            MaterialColors.getColor(this@ListOfPlaylistActivity, R.attr.dialogBtnBackground, Color.RED)
        )
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)?.setBackgroundColor(
            MaterialColors.getColor(this@ListOfPlaylistActivity, R.attr.dialogBtnBackground, Color.RED)
        )
    }
    //플레이리스트 추가 수행
    private fun addPlaylist(name: String){
        var playlistExists = false
        for(i in PlaylistManager.playlists) {
            if (name == i.name){
                playlistExists = true
                break
            }
        }
        //동일명의 플레이리스트 존재 시 추가 x, Toast 메시지 출력
        if(playlistExists) Toast.makeText(this, "Playlist Exist!!", Toast.LENGTH_SHORT).show()
        else {
            PlaylistManager.playlists.add(Playlist(name, ArrayList()))
            val mService = MusicServiceConnection.musicService!!
            mService.savePlaylistManager() { }
            adapter.refreshPlaylist()
        }
    }

    override fun onResume() {
        super.onResume()

        if(PlaylistManager.playlists.size > 0){
            adapter = PlaylistViewAdapter(this@ListOfPlaylistActivity, PlaylistManager.playlists,
                object: PlaylistViewAdapter.OnItemClickListener {
                    override fun onItemClick(view: View, pos: Int) {
                        when(operationOrdinal) {
                            ActivityOperation.LIST_OF_PLAYLIST_EXPLORE.ordinal -> {
                                exploringListPos = pos
                                startActivity(Intent(this@ListOfPlaylistActivity, PlaylistDetailsActivity::class.java).apply {
                                    putExtra("operation", ActivityOperation.PLAYLIST_DETAILS_PERSONAL_TAG.ordinal)
                                })
                            }
                            ActivityOperation.LIST_OF_PLAYLIST_MOVE.ordinal -> {
                                val exploringMusicList = PlaylistManager.playlists[exploringListPos].musicList
                                val selectedMusicId = ArrayList<Int>().apply {
                                    intent.getIntegerArrayListExtra("selectedItemList")?.forEach {
                                        add(exploringMusicList[it])
                                    }
                                }
                                selectedMusicId.forEach {
                                    PlaylistManager.playlists[pos].musicList.add(it)
                                    exploringMusicList.remove(it)
                                }
                                MusicServiceConnection.musicService!!.savePlaylistManager {  }
                                finish()
                            }
                            ActivityOperation.LIST_OF_PLAYLIST_SHARE.ordinal -> {
                                if (PlaylistManager.playlists[pos].musicList.isEmpty()) return
                                exploringListPos = pos
                                startActivity(Intent(this@ListOfPlaylistActivity, ShareDetailsActivity::class.java))
                                finish()
                            }
                        }
                    }
                },
                object: PlaylistViewAdapter.OnItemClickListener{
                    override fun onItemClick(view: View, pos: Int) {
//                        TODO ShareActivity에서 넘어왔을 경우 pos를 넘기고 SharedDetailsActivity로 이동
                        val intent = Intent(this@ListOfPlaylistActivity, ShareDetailsActivity::class.java)
                        intent.putExtra("index", pos)
                        startActivity(intent)
                    }
                }

            )

            binding.playlistRV.adapter = adapter
        }

        ControlViewManager.displayControlView(binding.exoControlView)
    }
}


