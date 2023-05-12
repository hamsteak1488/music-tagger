package com.cookandroid.myapplication.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.myapplication.*
import com.cookandroid.myapplication.adapters.PlaylistViewAdapter
import com.cookandroid.myapplication.databinding.ActivityListOfPlaylistBinding
import com.cookandroid.myapplication.databinding.AddPlaylistBinding
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tftf.util.Playlist
import kotlin.collections.ArrayList

class ListOfPlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListOfPlaylistBinding
    private lateinit var adapter: PlaylistViewAdapter

    private var operationOrdinal:Int = -1

    private lateinit var listOfPlaylist: ArrayList<Playlist>

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


        binding.backBtnPLA.setOnClickListener {
            finish()
        }
        binding.addPlaylistBtn.setOnClickListener {
            displayAddPlaylistDialog()
        }
    }

    //플레이리스트 추가 창
    private fun displayAddPlaylistDialog(){
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
        
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setBackgroundColor(
            MaterialColors.getColor(this@ListOfPlaylistActivity, R.attr.dialogBtnBackground, Color.RED)
        )
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setBackgroundColor(
            MaterialColors.getColor(this@ListOfPlaylistActivity, R.attr.dialogBtnBackground, Color.RED)
        )
    }
    //플레이리스트 추가 수행
    private fun addPlaylist(name: String){
        for(playlist in listOfPlaylist) {
            if (name == playlist.name){
                //동일명의 플레이리스트 존재 시 추가 x, Toast 메시지 출력
                Toast.makeText(this, "Playlist Exist!!", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val newPlaylist = Playlist(SettingsManager.userID, name, "playlist description", ArrayList())
        listOfPlaylist.add(newPlaylist)
        PlaylistManager.savePlaylist(newPlaylist)

        initLayout()
    }

    override fun onResume() {
        super.onResume()

        RetrofitManager.loadUserPlaylist(SettingsManager.userID) { loadedListOfPlaylist ->
            listOfPlaylist =
                if (loadedListOfPlaylist == null) ArrayList()
                else ArrayList(loadedListOfPlaylist)

            initLayout()
        }
    }

    private fun initLayout() {
        if (listOfPlaylist.isNotEmpty() && operationOrdinal == ActivityOperation.LIST_OF_PLAYLIST_EXPLORE.ordinal) {
            initRecyclerView()
            binding.instructionPA.visibility = View.VISIBLE
        }
        else {
            //생성된 플레이리스트가 존재하면 (플레이리스트 생성 문구) 노출 x
            binding.instructionPA.visibility = View.GONE
        }

        ControlViewManager.displayControlView(binding.exoControlView)
    }

    private fun initRecyclerView() {
            adapter = PlaylistViewAdapter(this@ListOfPlaylistActivity, listOfPlaylist,
                object: PlaylistViewAdapter.OnItemClickListener {
                    override fun onItemClick(view: View, pos: Int) {
                        when(operationOrdinal) {
                            ActivityOperation.LIST_OF_PLAYLIST_EXPLORE.ordinal -> {
                                PlaylistManager.exploringPlaylist = listOfPlaylist[pos]
                                startActivity(Intent(this@ListOfPlaylistActivity, PlaylistDetailsActivity::class.java).apply {
                                    putExtra("operation", ActivityOperation.PLAYLIST_DETAILS_PERSONAL_TAG.ordinal)
                                })
                            }
                            ActivityOperation.LIST_OF_PLAYLIST_MOVE.ordinal -> {
                                val exploringMusicList = PlaylistManager.exploringPlaylist!!.musicIDList
                                val selectedMusicId = ArrayList<Int>().apply {
                                    intent.getIntegerArrayListExtra("selectedItemList")?.forEach {
                                        add(exploringMusicList[it])
                                    }
                                }

                                PlaylistManager.addPlaylistItem(listOfPlaylist[pos], selectedMusicId)
                                PlaylistManager.removePlaylistItem(PlaylistManager.exploringPlaylist!!, selectedMusicId)

                                finish()
                            }
                            ActivityOperation.LIST_OF_PLAYLIST_SHARE.ordinal -> {
                                if (listOfPlaylist[pos].musicIDList.isEmpty()) {
                                    Toast.makeText(this@ListOfPlaylistActivity, "빈 리스트를 공유할 수 없습니다!", Toast.LENGTH_SHORT).show()
                                    return
                                }
                                PlaylistManager.exploringPlaylist = listOfPlaylist[pos]
                                startActivity(Intent(this@ListOfPlaylistActivity, ShareDetailsActivity::class.java))
                                finish()
                            }
                        }
                    }
                },
                object: PlaylistViewAdapter.OnItemClickListener {
                    override fun onItemClick(view: View, pos: Int) {
                        val builder = MaterialAlertDialogBuilder(this@ListOfPlaylistActivity)
                        builder.setTitle(listOfPlaylist[pos].name)
                            .setMessage("delete playlist?")
                            .setPositiveButton("Yes") { dialog, _ ->

                                if (listOfPlaylist[pos].name == PlaylistManager.playlistInUse?.name) {
                                    MusicServiceConnection.musicService!!.currentMusicPos = -1
                                }

                                PlaylistManager.removePlaylist(listOfPlaylist[pos].name)
                                listOfPlaylist.removeAt(pos)

                                dialog.dismiss()

                                initLayout()
                            }
                            .setNegativeButton("No"){dialog,_ ->
                                dialog.dismiss()
                            }
                        val customDialog = builder.create()
                        customDialog.show()
                        customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                        customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
                    }
                }

                /*
                object: PlaylistViewAdapter.OnItemClickListener{
                    override fun onItemClick(view: View, pos: Int) {
//                        TODO ShareActivity에서 넘어왔을 경우 pos를 넘기고 SharedDetailsActivity로 이동
                        val intent = Intent(this@ListOfPlaylistActivity, ShareDetailsActivity::class.java)
                        intent.putExtra("index", pos)
                        startActivity(intent)
                    }
                }
                */
            )

            binding.playlistRV.adapter = adapter
    }
}


