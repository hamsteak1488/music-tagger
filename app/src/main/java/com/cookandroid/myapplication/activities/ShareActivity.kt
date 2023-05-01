package com.cookandroid.myapplication.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.myapplication.databinding.ActivityShareBinding
import com.cookandroid.myapplication.ShareAdapter

class ShareActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShareBinding
    private lateinit var adapter: ShareAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sharelistRV.setHasFixedSize(true)
        binding.sharelistRV.setItemViewCacheSize(13)
        binding.sharelistRV.layoutManager = LinearLayoutManager(this)
        binding.uploadShareBtn.setOnClickListener{
            val intent = Intent(this@ShareActivity, ListOfPlaylistActivity::class.java)
            intent.putExtra("isSharing", true)
            startActivity(intent) }
    }
}