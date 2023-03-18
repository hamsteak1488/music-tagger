package com.cookandroid.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.myapplication.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    // TODO: 음악 클릭 시, 플레이리스트들 나열할 액티비티 생성하고 동작 구현할 것
    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchRV.setItemViewCacheSize(15) //Int 만큼 항목 유지
        binding.searchRV.setHasFixedSize(true)//리사이클러뷰 크기 고정
        binding.searchRV.layoutManager = LinearLayoutManager(this)
        binding.backBtnSA.setOnClickListener {finish()}

        // 검색창 이벤트
        binding.searchViewSA.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            // 검색버튼 이벤트
            override fun onQueryTextSubmit(query: String?): Boolean {
                // MusicService에서 검색 후 결과를 adapter로 연결
                MusicServiceConnection.musicService!!.getMusicMetadataList(listOf("title", "artist"), query!!) {
                    adapter = MusicAdapter(this@SearchActivity, it as ArrayList<Music>, object:MusicAdapter.OnItemClickListener {
                        override fun onItemClick(view: View, pos: Int) {
                            if (intent.getBooleanExtra("searchForAdd", false)) {
                                val listPos = intent.getIntExtra("listPos", -1)
                                MusicServiceConnection.musicService!!.getMusicMetadata(it[pos].id) {
                                    PlaylistManager.allPlayList[listPos].musicList.add(it!!)
                                    finish()
                                }
                            }
                            else {
                                //todo: 메인->검색창->음악선택 : 임시 플레이리스트 생성하여 재생
                                val playMusicIntent = Intent(this@SearchActivity, PlayMusicActivity::class.java)
                                MusicServiceConnection.musicService!!.setPlayList(arrayListOf(it[pos].id))
                                startActivity(playMusicIntent)
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
}