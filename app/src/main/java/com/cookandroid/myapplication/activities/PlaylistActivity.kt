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
import com.cookandroid.myapplication.databinding.ActivityPlaylistBinding
import com.cookandroid.myapplication.databinding.AddPlaylistBinding
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

class PlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistBinding
    private lateinit var adapter: PlaylistViewAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(13)
        binding.playlistRV.layoutManager = LinearLayoutManager(this@PlaylistActivity)
        binding.backBtnPLA.setOnClickListener { finish() }
        binding.addPlaylistBtn.setOnClickListener { customAlertDialog() }

        //생성된 플레이리스트가 존재하면 (플레이리스트 생성 문구) 노출 x
        if(PlaylistManager.playlists.isNotEmpty()) binding.instructionPA.visibility = View.GONE
    }

    //플레이리스트 추가 창
    private fun customAlertDialog(){
        val customDialog = LayoutInflater.from(this@PlaylistActivity).inflate(R.layout.add_playlist, binding.root, false)
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
            MaterialColors.getColor(this@PlaylistActivity, R.attr.dialogBtnBackground, Color.RED)
        )
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)?.setBackgroundColor(
            MaterialColors.getColor(this@PlaylistActivity, R.attr.dialogBtnBackground, Color.RED)
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
            mService.savePlaylistManager(mService.email) { }
            adapter.refreshPlaylist()
        }
    }

    override fun onResume() {
        super.onResume()

        if(PlaylistManager.playlists.size > 0){
            adapter = PlaylistViewAdapter(this@PlaylistActivity, PlaylistManager.playlists,
                object: PlaylistViewAdapter.OnItemClickListener {
                    override fun onItemClick(view: View, pos: Int) {
                        val playlistDetailIntent = Intent(this@PlaylistActivity, PlaylistDetailsActivity::class.java)
                        playlistDetailIntent.putExtra("index", pos)
                        startActivity(playlistDetailIntent)
                    }
                })
            binding.playlistRV.adapter = adapter
        }
    }
}