package com.cookandroid.myapplication

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.cookandroid.myapplication.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var musicAdapter: Adapter
    private lateinit var auth: FirebaseAuth

    companion object{
        lateinit var MusicListMA: ArrayList<Music>
        lateinit var musicListSearch: ArrayList<Music>
        var search: Boolean = false
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ///랜덤, 플레이리스트, 검색 버튼
        binding.shuffleBtn.setOnClickListener{
            val intent = Intent(this@MainActivity,PlayMusicActivity::class.java)
            startActivity(intent) }
        binding.playlistBtn.setOnClickListener{
            startActivity(Intent(this@MainActivity, PlaylistActivity::class.java)) }
        binding.searchSongBtn.setOnClickListener{
            startActivity((Intent(this@MainActivity,SearchActivity::class.java))) }

        //로그아웃 버튼
        auth = FirebaseAuth.getInstance()
        binding.logoutBtn.setOnClickListener{
            auth.signOut()
            Intent(this, LoginActivity::class.java).also{
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
                Toast.makeText(this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show()
            }
        }

        // 로그인 후 플레이리스트 서버로부터 받아오기
        PlaylistActivity.musicPlaylist = AllPlaylist()
    }
}