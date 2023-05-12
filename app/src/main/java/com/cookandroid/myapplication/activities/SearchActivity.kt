package com.cookandroid.myapplication.activities

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.myapplication.*
import com.cookandroid.myapplication.adapters.MusicAdapter
import com.cookandroid.myapplication.databinding.ActivitySearchBinding
import com.tftf.util.Playlist


class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: MusicAdapter
    private val mService = MusicServiceConnection.musicService!!

    private var operationOrdinal:Int = -1
    
    // todo : 텍스트 및 힌트 색깔 변경 필요

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        operationOrdinal = intent.getIntExtra("operation", -1)


        val searchEditText = binding.searchViewSA.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(resources.getColor(R.color.white))
        searchEditText.setHintTextColor(resources.getColor(R.color.white))

        binding.searchRV.setItemViewCacheSize(15) //Int 만큼 항목 유지
        binding.searchRV.setHasFixedSize(true)//리사이클러뷰 크기 고정
        binding.searchRV.layoutManager = LinearLayoutManager(this)
        binding.backBtnSA.setOnClickListener {finish()}

        // 검색창 이벤트
        binding.searchViewSA.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            // 검색버튼 이벤트
            override fun onQueryTextSubmit(query: String?): Boolean {
                // MusicService에서 검색 후 결과를 adapter로 연결
                RetrofitManager.getMusicMetadataList(listOf("title", "artist"), query!!) { resultMusicList ->
                    if (resultMusicList == null) return@getMusicMetadataList

                    adapter = MusicAdapter(this@SearchActivity, ArrayList(resultMusicList), null, object:
                        MusicAdapter.OnItemClickListener {
                        override fun onItemClick(view: View, pos: Int) {
                            when(operationOrdinal) {
                                ActivityOperation.SEARCH_ADD.ordinal -> {
                                    PlaylistManager.addPlaylistItem(PlaylistManager.exploringPlaylist!!, arrayListOf(resultMusicList[pos].id))
                                    finish()
                                }
                                ActivityOperation.SEARCH_EXPLORE.ordinal -> {
                                    val playMusicIntent = Intent(this@SearchActivity, PlayMusicActivity::class.java)
                                    PlaylistManager.playlistInUse =
                                        Playlist(
                                            SettingsManager.userID,
                                            "exploring music",
                                            "exploring music with search",
                                            arrayListOf(resultMusicList[pos].id)
                                        )
                                    mService.reloadPlayer(0)
                                    startActivity(playMusicIntent)
                                }
                            }
                        }
                    })
                    binding.searchRV.adapter = adapter
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()

        ControlViewManager.displayControlView(binding.exoControlView)
    }
}