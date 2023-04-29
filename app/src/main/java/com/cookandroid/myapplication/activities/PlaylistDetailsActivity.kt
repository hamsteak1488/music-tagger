package com.cookandroid.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.*
import com.cookandroid.myapplication.PlaylistManager.exploringListPos
import com.cookandroid.myapplication.databinding.ActivityPlaylistDetailsBinding
import com.tftf.util.Music
import com.tftf.util.MusicTag


class PlaylistDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistDetailsBinding
    private lateinit var adapter: MusicAdapter

    private val mService:MusicService = MusicServiceConnection.musicService!!

    private var selectionMode:Boolean = false
    private val selectedItemList:ArrayList<Int> = ArrayList()

    private var rvLastScrollPos:Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playlistNamePD.text = PlaylistManager.playlists[exploringListPos].name

        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this)

        binding.playlistDetailsRV.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })

        //음악 추가
        binding.addBtnPD.setOnClickListener{
            val addMusicIntent = Intent(this, SearchActivity::class.java)
            addMusicIntent.putExtra("searchForAdd", true)
            addMusicIntent.putExtra("listPos", exploringListPos)
            startActivity(addMusicIntent)
        }

        binding.backBtnPD.setOnClickListener {
            finish()
        }
    }



    override fun onResume() {
        super.onResume()

        selectionMode = false
        selectedItemList.clear()
        binding.selectionCountTV.visibility = View.INVISIBLE

        binding.moreInfoPD.text = "Total ${PlaylistManager.playlists[exploringListPos].musicList.size} Songs.\n\n"
        if(PlaylistManager.playlists[exploringListPos].musicList.size > 0){
            mService.getMusicMetadataList(PlaylistManager.playlists[exploringListPos].musicList) { musicList ->
                if (musicList == null) return@getMusicMetadataList
                else initMusicAdapter(musicList)
            }
            Glide.with(this)
                .load("http://10.0.2.2:8080/img?id=" + (PlaylistManager.playlists[exploringListPos].musicList[0]))
                .apply(RequestOptions().placeholder(R.drawable.ic_baseline_music_video_24).centerCrop())
                .into(binding.playlistImgPD)
        }

        ControlViewManager.displayControlView(binding.exoControlView)
    }



    private fun initMusicAdapter(musicList:List<Music>) {
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

                    rvLastScrollPos = (binding.playlistDetailsRV.layoutManager!! as LinearLayoutManager).findFirstVisibleItemPosition()

                    initMusicAdapter(musicList)
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
        binding.playlistDetailsRV.scrollToPosition(rvLastScrollPos)

        this.onBackPressedDispatcher.addCallback(this@PlaylistDetailsActivity, object:OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (selectionMode) {
                    selectionMode = false
                    rvLastScrollPos = (binding.playlistDetailsRV.layoutManager!! as LinearLayoutManager).findFirstVisibleItemPosition()
                    initMusicAdapter(musicList)
                }
                else {
                    finish()
                }
            }
        })
    }
}