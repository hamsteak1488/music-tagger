package com.cookandroid.myapplication.activities

import android.os.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.*
import com.cookandroid.myapplication.MusicServiceConnection.serverUrl
import com.cookandroid.myapplication.UserManager
import com.cookandroid.myapplication.adapters.TagAdapter
import com.cookandroid.myapplication.databinding.ActivityPlayMusicBinding

class PlayMusicActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayMusicBinding
    private val mService = MusicServiceConnection.musicService!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtnPA.setOnClickListener { finish() }

        mService.setViewPlayer(binding.exoControlView)

        if (mService.currentMusicPos != -1) {
            setLayout()
            mService.setMediaItemChangeListenerForPlayMusicActivity(object:MusicService.OnMediaItemChangeListener {
                override fun onMediaItemChange() {
                    setLayout()
                }
            })
            mService.prepareAndPlay()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mService.removeMediaItemChangeListenerForPlayMusicActivity()
    }

    private fun setLayout() {
        val currentMusicID = PlaylistManager.playlistInUse.musicList[mService.currentMusicPos]

        RetrofitManager.getMusicMetadata(currentMusicID) { music ->
            if (music == null) return@getMusicMetadata
            binding.songNamePA.text = music.title
            binding.songNamePA.invalidate()
            binding.artistNamePA.text = music.artist

            Glide.with(this@PlayMusicActivity)
                .load(serverUrl + "img?id=" + (music.id))
                .apply(RequestOptions().placeholder(R.drawable.ic_baseline_music_note_24).centerCrop())
                .into(binding.songImg)
            binding.songImg.invalidate()
        }


        RetrofitManager.getPersonalMusicTag(UserManager.userID, currentMusicID) { tag ->
            if (tag != null) {
                binding.tagRV.layoutManager = LinearLayoutManager(
                    this@PlayMusicActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                binding.tagRV.adapter = TagAdapter(tag)
                binding.weekdayEmoji.text = tag.tagMap["요일"]
            }
        }
    }

}