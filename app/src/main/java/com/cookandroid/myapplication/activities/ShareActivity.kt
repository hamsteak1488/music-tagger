package com.cookandroid.myapplication.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.myapplication.*
import com.cookandroid.myapplication.adapters.ShareAdapter
import com.cookandroid.myapplication.databinding.ActivityShareBinding

class ShareActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShareBinding
    private lateinit var shareAdapter: ShareAdapter
    private val sharedListsSize = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtnSA.setOnClickListener{finish()}

        binding.sharelistRV.setHasFixedSize(true)
        binding.sharelistRV.setItemViewCacheSize(13)
        binding.sharelistRV.layoutManager = LinearLayoutManager(this)

        binding.uploadShareBtn.setOnClickListener {
            // todo : 선택할 리스트 목록에 아무것도 나타나지 않음
            startActivity(Intent(this@ShareActivity, ListOfPlaylistActivity::class.java).apply {
                putExtra("operation", ActivityOperation.LIST_OF_PLAYLIST_SHARE.ordinal)
            })
        }
    }

    override fun onResume() {
        super.onResume()

        initRecyclerView()

        ControlViewManager.displayControlView(binding.exoControlView)
    }
    
    fun initRecyclerView() {
        RetrofitManager.getAllSharedPlaylist(sharedListsSize) { listOfSharedList ->

            if (listOfSharedList.isNullOrEmpty()) return@getAllSharedPlaylist

            binding.sharelistRV.setItemViewCacheSize(5)
            binding.sharelistRV.setHasFixedSize(true)
            binding.sharelistRV.layoutManager = LinearLayoutManager(this@ShareActivity)

            shareAdapter = ShareAdapter(this@ShareActivity, ArrayList(listOfSharedList),
                object : ShareAdapter.OnDownloadClickListener {
                    override fun onItemClick(textView: TextView, pos: Int) {
                        PlaylistManager.addPlaylist(listOfSharedList[pos])
                        PlaylistManager.AlterSharedPlaylistDownloadCount(listOfSharedList[pos], 1)
                        Toast.makeText(this@ShareActivity, "공유리스트 내려받기 완료!", Toast.LENGTH_SHORT).show()
                        textView.text = "downloaded : " + (listOfSharedList[pos].downloadCount + 1)
                    }
                },
                object : ShareAdapter.OnLikeClickListener {
                    override fun onItemClick(imageView: ImageView, textView:TextView, pos: Int, checked: Boolean) {
                        if (checked) {
                            imageView.imageTintList = ColorStateList.valueOf(Color.RED)
                            textView.text = "Like : " + (listOfSharedList[pos].likeCount + 1)
                            PlaylistManager.AlterSharedPlaylistLikeCount(listOfSharedList[pos], 1)
                        }
                        else {
                            imageView.imageTintList = ColorStateList.valueOf(Color.GRAY)
                            textView.text = "Like : " + (listOfSharedList[pos].likeCount - 1)
                            PlaylistManager.AlterSharedPlaylistLikeCount(listOfSharedList[pos], -1)
                        }
                    }
                }
            )
            binding.sharelistRV.adapter = shareAdapter
        }
    }
}