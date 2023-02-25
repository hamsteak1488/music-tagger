package com.cookandroid.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.myapplication.databinding.ActivityPlayMusicBinding
import com.google.android.exoplayer2.MediaItem

class PlayMusicActivity : AppCompatActivity() {

    // 액티비티와 연결된 서비스
    private var musicService:MusicService? = null

    private val bunnyUri =
        Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
    private val musicUri =
        Uri.parse("http://121.181.181.105:8080/media?name=test_audio1.mp3")
    private var isBound = false

    private lateinit var binding:ActivityPlayMusicBinding


    // 서비스 연결 및 해제 이벤트를 관리할 변수
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val serviceBinder = service as MusicService.MyBinder
            musicService = serviceBinder.currentService()
            isBound = true

            val playList:ArrayList<MediaItem> = ArrayList<MediaItem>()
            playList.add(MediaItem.fromUri(musicUri))
            playList.add(MediaItem.fromUri(bunnyUri))
            musicService!!.setPlayList(playList)


            // control 연결 시점 추후 수정할 것
            musicService!!.setPlayerView(binding.exoControlView)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            musicService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //뒤로 가기 버튼
        binding.backBtnPA.setOnClickListener { finish() }

        // Service 연결
        val intent = Intent(this, MusicService::class.java)
        startService(intent)
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)


        binding.songNamePA.setOnClickListener {
            musicService!!.getMusicMetadata("star walkin") {
                binding.songNamePA.text = it?.title
            }
        }
    }

    private fun setLayout(music:Music) : Unit {
        binding.songNamePA.text = musicService!!.getTitle()
    }
}