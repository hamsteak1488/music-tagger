package com.cookandroid.myapplication.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.ColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.myapplication.*
import com.cookandroid.myapplication.databinding.ActivityShareBinding
import com.tftf.util.Playlist
import com.tftf.util.PlaylistForShare

class ShareActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShareBinding
    private lateinit var shareAdapter: ShareAdapter
    private val mService = MusicServiceConnection.musicService!!
    private val sharedListsSize = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sharelistRV.setHasFixedSize(true)
        binding.sharelistRV.setItemViewCacheSize(13)
        binding.sharelistRV.layoutManager = LinearLayoutManager(this)

        binding.uploadShareBtn.setOnClickListener {
            startActivity(Intent(this@ShareActivity, ListOfPlaylistActivity::class.java).apply {
                putExtra("operation", ActivityOperation.LIST_OF_PLAYLIST_SHARE.ordinal)
            })
        }
    }

    override fun onResume() {
        super.onResume()

        ControlViewManager.displayControlView(binding.exoControlView)

        mService.downloadSharedLists (sharedListsSize) { sharedLists ->

            if (sharedLists.isEmpty()) return@downloadSharedLists

            binding.sharelistRV.setItemViewCacheSize(5)
            binding.sharelistRV.setHasFixedSize(true)
            binding.sharelistRV.layoutManager = LinearLayoutManager(this@ShareActivity)

            shareAdapter = ShareAdapter(this@ShareActivity, sharedLists as ArrayList<PlaylistForShare>,
                object : ShareAdapter.OnItemClickListener {
                    override fun onItemClick(view: View, pos: Int) {
                        PlaylistManager.playlists.add(sharedLists[pos])
                        Toast.makeText(this@ShareActivity, "copy completed!", Toast.LENGTH_SHORT).show()
                    }
                },
                object : ShareAdapter.OnLikeClickListener {
                    override fun onItemClick(view: View, pos: Int, checked: Boolean) {
                        view as ImageView
                        if (checked)
                            view.imageTintList = ColorStateList.valueOf(Color.RED)
                        else
                            view.imageTintList = ColorStateList.valueOf(Color.GRAY)
                    }
                }
            )
            binding.sharelistRV.adapter = shareAdapter
        }
    }
}