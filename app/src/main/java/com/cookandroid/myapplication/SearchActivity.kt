package com.cookandroid.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.myapplication.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: Adapter
    private lateinit var musicListSearch: ArrayList<Music>
    private var search: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        musicListSearch = ArrayList()

        binding.searchRV.setItemViewCacheSize(15) //Int 만큼 항목 유지
        binding.searchRV.setHasFixedSize(true)//리사이클러뷰 크기 고정
        binding.searchRV.layoutManager = LinearLayoutManager(this)

        adapter = Adapter(this, musicListSearch, searchActivity = true)
        binding.searchRV.adapter = adapter
        binding.backBtnSA.setOnClickListener {finish()}

        //Search View 반영
        binding.searchViewSA.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText != null){
                    val userInput = newText.lowercase()
                    musicListSearch = getSearchResult(userInput)
                    search = true
                    //검색 결과로 리스트 초기화
                    adapter.updateMusicList(searchList = musicListSearch)
                }
                return true
            }
        })
    }

    //서버로 검색어 전송, 결과 리스트 반환
    fun getSearchResult(title:String) : ArrayList<Music> {
        return ArrayList<Music>()
    }
}