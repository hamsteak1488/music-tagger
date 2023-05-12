package com.cookandroid.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.*
import com.cookandroid.myapplication.SettingsManager.serverUrl
import com.cookandroid.myapplication.adapters.MusicAdapter
import com.cookandroid.myapplication.databinding.ActivityPlaylistDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tftf.util.Music
import com.tftf.util.MusicTag


class PlaylistDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistDetailsBinding
    private lateinit var adapter: MusicAdapter

    private val mService:MusicService = MusicServiceConnection.musicService!!

    private var selectionMode:Boolean = false
    private val selectedItemList:ArrayList<Int> = ArrayList()

    private var rvLastScrollPos:Int = 0

    private var operationOrdinal:Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        operationOrdinal = intent.getIntExtra("operation", -1)


//        binding.playlistNamePD.text = PlaylistManager.playlists[exploringListPos].name

        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this)

        //음악 추가
        binding.addBtnPD.setOnClickListener{
            val addMusicIntent = Intent(this, SearchActivity::class.java)
            addMusicIntent.putExtra("operation", ActivityOperation.SEARCH_ADD.ordinal)
            startActivity(addMusicIntent)
        }

        binding.backBtnPD.setOnClickListener {
            finish()
        }

        binding.movePlaylistBtn.setOnClickListener {
            selectedItemList.sort()
            startActivity(Intent(this@PlaylistDetailsActivity, ListOfPlaylistActivity::class.java).apply {
                putExtra("operation", ActivityOperation.LIST_OF_PLAYLIST_MOVE.ordinal)
                putIntegerArrayListExtra("selectedItemList", selectedItemList)
            })
        }

        binding.removeBtn.setOnClickListener {
            val exploringMusicIDList = PlaylistManager.exploringPlaylist!!.musicIDList
            val selectedMusicIDList = ArrayList<Int>().apply {
                selectedItemList.forEach {
                    add(exploringMusicIDList[it])
                }
            }

            PlaylistManager.removePlaylistItem(PlaylistManager.exploringPlaylist!!, selectedMusicIDList)

            initLayout()
        }
    }



    override fun onResume() {
        super.onResume()

        initLayout()

        ControlViewManager.displayControlView(binding.exoControlView)
    }

    private fun initLayout() {

        selectionMode = false
        selectedItemList.clear()
        binding.selectionCountTV.visibility = View.INVISIBLE
        binding.movePlaylistBtn.visibility = View.INVISIBLE
        binding.removeBtn.visibility = View.INVISIBLE

        //binding.moreInfoPD.text = "Total ${PlaylistManager.playlists[exploringListPos].musicList.size} Songs.\n\n"
        if(PlaylistManager.exploringPlaylist!!.musicIDList.size > 0) {
            RetrofitManager.getMusicMetadataList(PlaylistManager.exploringPlaylist!!.musicIDList) { musicList ->
                if (musicList == null) return@getMusicMetadataList
                else initRecyclerView(ArrayList(musicList))
            }
            Glide.with(this)
                .load(serverUrl + "img?id=" + (PlaylistManager.exploringPlaylist!!.musicIDList[0]))
                .apply(RequestOptions().placeholder(R.drawable.ic_baseline_music_video_24).centerCrop())
                .into(binding.playlistImgPD)
            binding.playlistImgPD.visibility = View.VISIBLE
        }
        else {
            binding.playlistDetailsRV.adapter = null
            binding.playlistImgPD.visibility = View.INVISIBLE
        }
    }

    private fun getTagList(callbackOperation:(ArrayList<MusicTag>)->Unit) {
        when (operationOrdinal) {
            ActivityOperation.PLAYLIST_DETAILS_PERSONAL_TAG.ordinal -> {
                RetrofitManager.getPersonalMusicTagList(SettingsManager.userID, PlaylistManager.exploringPlaylist!!.musicIDList) { tagList ->
                    if (tagList == null) return@getPersonalMusicTagList
                    else callbackOperation(ArrayList(tagList))
                }
            }
            ActivityOperation.PLAYLIST_DETAILS_GENERAL_TAG.ordinal -> {
                RetrofitManager.getGeneralMusicTagList(PlaylistManager.exploringPlaylist!!.musicIDList) { tagList ->
                    if (tagList == null) return@getGeneralMusicTagList
                    else callbackOperation(ArrayList(tagList))
                }
            }
        }
    }

    private fun initRecyclerView(musicList:ArrayList<Music>) {

        getTagList { tagList ->
            initAdapter(musicList, tagList)
        }

        this.onBackPressedDispatcher.addCallback(this@PlaylistDetailsActivity, object:OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (selectionMode) {
                    selectionMode = false
                    rvLastScrollPos = (binding.playlistDetailsRV.layoutManager!! as LinearLayoutManager).findFirstVisibleItemPosition()
                    initRecyclerView(musicList)
                }
                else {
                    finish()
                }
            }
        })
    }


    private fun initAdapter(musicList:ArrayList<Music>, tagList:ArrayList<MusicTag>) {
        adapter = MusicAdapter(this@PlaylistDetailsActivity, musicList, tagList,
            object: MusicAdapter.OnItemClickListener {
                override fun onItemClick(view: View, pos: Int) {
                    PlaylistManager.playlistInUse = PlaylistManager.exploringPlaylist
                    if (!(PlaylistManager.playlistInUse == PlaylistManager.exploringPlaylist
                        && pos == mService.currentMusicPos)) {
                        mService.reloadPlayer(pos)
                    }
                    startActivity(Intent(this@PlaylistDetailsActivity, PlayMusicActivity::class.java))
                }
            },
            object: MusicAdapter.OnItemClickListener {
                override fun onItemClick(view: View, pos: Int) {
                    val customDialog = MaterialAlertDialogBuilder(this@PlaylistDetailsActivity)
                        .setTitle("option")
                        .setItems(arrayOf("다른 플레이리스트로 이동", "삭제")) { _, action ->
                            when(action) {
                                0 -> {
                                    startActivity(Intent(this@PlaylistDetailsActivity, ListOfPlaylistActivity::class.java).apply {
                                        putExtra("operation", ActivityOperation.LIST_OF_PLAYLIST_MOVE.ordinal)
                                        putExtra("selectedItemList", arrayListOf(pos))
                                    })
                                }
                                1 -> {
                                    PlaylistManager.removePlaylistItem(PlaylistManager.exploringPlaylist!!, arrayListOf(musicList[pos].id))
                                    initLayout()
                                }
                            }
                        }
                        .create()
                    customDialog.show()
                }
            },
            object: MusicAdapter.OnItemLongClickListener {
                override fun onItemLongClick(view: View, pos: Int) {
                    selectionMode = selectionMode.xor(true)
                    if (selectionMode) {
                        binding.selectionCountTV.visibility = View.VISIBLE
                        binding.movePlaylistBtn.visibility = View.VISIBLE
                        binding.removeBtn.visibility = View.VISIBLE
                    }
                    else {
                        binding.selectionCountTV.visibility = View.INVISIBLE
                        binding.movePlaylistBtn.visibility = View.INVISIBLE
                        binding.removeBtn.visibility = View.INVISIBLE
                    }

                    rvLastScrollPos = (binding.playlistDetailsRV.layoutManager!! as LinearLayoutManager).findFirstVisibleItemPosition()

                    initRecyclerView(musicList)
                }
            },
            object: MusicAdapter.OnItemCheckedChangeListener {
                override fun onItemCheckedChange(isChecked: Boolean, pos: Int) {
                    if (isChecked) {
                        selectedItemList.add(pos)
                    }
                    else {
                        selectedItemList.remove(pos)
                    }
                    binding.selectionCountTV.text = selectedItemList.size.toString() + "개 선택"
                }
            },
            selectionMode
        )

        binding.playlistDetailsRV.adapter = adapter
        binding.playlistDetailsRV.scrollToPosition(rvLastScrollPos)
    }
}