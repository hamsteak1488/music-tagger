package com.cookandroid.myapplication.activities

import android.os.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.set
import androidx.core.text.toSpannable
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.*
import com.cookandroid.myapplication.MusicServiceConnection.serverUrl
import com.cookandroid.myapplication.databinding.ActivityPlayMusicBinding
import com.tftf.util.MusicTag

class PlayMusicActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayMusicBinding
    private val mService = MusicServiceConnection.musicService!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtnPA.setOnClickListener { finish() }

        mService.setViewPlayer(binding.exoControlView)

        if (mService.currentListPos != -1 && mService.currentMusicPos != -1) {
            setLayout()
            mService.setMediaItemChangeListenerForPlayMusicActivity(object:MusicService.OnMediaItemChangeListener {
                override fun onMediaItemChange() {
                    setLayout()
                }
            })
            mService.prepareAndPlay()
        }
//
//        val weekday: TextView = findViewById(R.id.weekdayEmoji)
//        val text = "Wed."
//        val colorStart = ContextCompat.getColor(this,R.color.start)
//        val colorEnd = ContextCompat.getColor(this, R.color.end)
//        val spannable = text.toSpannable()
//        spannable[0..text.length]   = LinearGradientSpan(text,text,colorStart,colorEnd)
//        weekday.text = spannable
    }

    override fun onDestroy() {
        super.onDestroy()

        mService.removeMediaItemChangeListenerForPlayMusicActivity()
    }

    private fun setLayout() {
        val currentMusicID = PlaylistManager.playlists[mService.currentListPos].musicList[mService.currentMusicPos]

        mService.getMusicMetadata(currentMusicID) {
            if (it == null) return@getMusicMetadata
            binding.songNamePA.text = it.title
            binding.songNamePA.invalidate()
            binding.artistNamePA.text = it.artist

            Glide.with(this@PlayMusicActivity)
                .load(serverUrl + "img?id=" + (it.id))
                .apply(RequestOptions().placeholder(R.drawable.ic_baseline_music_note_24).centerCrop())
                .into(binding.songImg)
            binding.songImg.invalidate()
        }


        val tag = PlayHistoryManager.getMusicTag(currentMusicID)
        if (tag != null) {
            binding.tagRV.layoutManager = LinearLayoutManager(this@PlayMusicActivity, LinearLayoutManager.HORIZONTAL, false)
            binding.tagRV.adapter = TagAdapter(tag)
            binding.weekdayEmoji.text = tag.tagMap["요일"]
        }
    }

}