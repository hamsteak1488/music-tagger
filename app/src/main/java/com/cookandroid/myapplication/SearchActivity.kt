package com.cookandroid.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.myapplication.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    // TODO: 음악 클릭 시, 플레이리스트들 나열할 액티비티 생성하고 동작 구현할 것
    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchRV.setItemViewCacheSize(15) //Int 만큼 항목 유지
        binding.searchRV.setHasFixedSize(true)//리사이클러뷰 크기 고정
        binding.searchRV.layoutManager = LinearLayoutManager(this)
        binding.backBtnSA.setOnClickListener {finish()}

        //Search View 반영
        binding.searchViewSA.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {

                MusicServiceConnection.musicService!!.getMusicMetadataList("title", query!!) {
                    adapter = Adapter(this@SearchActivity, it as ArrayList<Music>, searchActivity = true)
                    binding.searchRV.adapter = adapter
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    //서버로 검색어 전송, 결과 리스트 반환
    fun getSearchResult(title:String) : ArrayList<Music> {
        return ArrayList<Music>()
    }
}