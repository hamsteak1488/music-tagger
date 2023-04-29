package com.cookandroid.myapplication.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.*
import com.cookandroid.myapplication.PlaylistManager.exploringListPos
import com.cookandroid.myapplication.databinding.ActivityPlaylistDetailsBinding
import com.cookandroid.myapplication.databinding.MusicSelectedBinding
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tftf.util.Music
import com.tftf.util.MusicTag


class PlaylistDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistDetailsBinding
    private lateinit var adapter: MusicAdapter

    private val mService:MusicService = MusicServiceConnection.musicService!!

    private var selectionMode:Boolean = false
    private val selectedItemList:ArrayList<Int> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectionMode = false
        selectedItemList.clear()

        binding.playlistNamePD.text = PlaylistManager.playlists[exploringListPos].name

        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this)

        //음악 추가
        binding.addBtnPD.setOnClickListener{
            val addMusicIntent = Intent(this, SearchActivity::class.java)
            addMusicIntent.putExtra("searchForAdd", true)
            addMusicIntent.putExtra("listPos", exploringListPos)
            startActivity(addMusicIntent)
        }
        //음악 전체 삭제
        binding.removeAllPD.setOnClickListener{
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("remove")
                .setMessage("Remove all songs from playlist?")
                .setPositiveButton("Yes"){dialog, _ ->
                    PlaylistManager.playlists[exploringListPos].musicList.clear()
                    mService.savePlaylistManager(mService.email){ }
                    dialog.dismiss()
                    finish()
                    startActivity(intent)
                }
                .setNegativeButton("No"){dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()

            customDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)?.setBackgroundColor(
                MaterialColors.getColor(this@PlaylistDetailsActivity, R.attr.dialogBtnBackground, Color.RED)
            )
            customDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)?.setBackgroundColor(
                MaterialColors.getColor(this@PlaylistDetailsActivity, R.attr.dialogBtnBackground, Color.RED)
            )

            customDialog.show()
        }

        binding.backBtnPD.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        binding.moreInfoPD.text = "Total ${PlaylistManager.playlists[exploringListPos].musicList.size} Songs.\n\n"
        if(PlaylistManager.playlists[exploringListPos].musicList.size > 0){
            mService.getMusicMetadataList(PlaylistManager.playlists[exploringListPos].musicList) { musicList ->
                if (musicList == null) return@getMusicMetadataList
                else initMusicRV(musicList)
            }
            Glide.with(this)
                .load("http://10.0.2.2:8080/img?id=" + (PlaylistManager.playlists[exploringListPos].musicList[0]))
                .apply(RequestOptions().placeholder(R.drawable.ic_baseline_music_video_24).centerCrop())
                .into(binding.playlistImgPD)
        }

        ControlViewManager.displayControlView(binding.exoControlView)
    }

    private fun initMusicRV(musicList:List<Music>) {
        val tagList = ArrayList<MusicTag>().apply {
            musicList.forEach { music ->
                val tag = PlayHistoryManager.getMusicTag(music.id)
                if (tag == null) add(MusicTag())
                else add(tag)
            }
        }

        adapter = MusicAdapter(this@PlaylistDetailsActivity, musicList.toCollection(ArrayList()), tagList,
            object: MusicAdapter.OnItemClickListener {
                override fun onItemClick(view: View, musicPos: Int) {
                    if (musicPos != mService.currentMusicPos) {
                        mService.reloadPlayer(exploringListPos, musicPos)
                    }
                    startActivity(Intent(this@PlaylistDetailsActivity, PlayMusicActivity::class.java))
                }
            },
            object:MusicAdapter.OnItemLongClickListener {
                override fun onItemLongClick(view: View, pos: Int) {
                    selectionMode = selectionMode.xor(true)
                    if (selectionMode) binding.selectionCountTV.visibility = View.VISIBLE
                    else binding.selectionCountTV.visibility = View.INVISIBLE
                    initMusicRV(musicList)
                    adapter.notifyDataSetChanged()
                }
            },
            object:MusicAdapter.OnItemCheckedChangeListener {
                override fun onItemCheckedChange(isChecked: Boolean, pos: Int) {
                    if (isChecked) {
                        selectedItemList.add(musicList[pos].id)
                    }
                    else {
                        selectedItemList.remove(musicList[pos].id)
                    }
                    binding.selectionCountTV.text = selectedItemList.size.toString() + "개 선택"
                }
            },
            selectionMode
        )
        binding.playlistDetailsRV.adapter = adapter
    }
}