package com.cookandroid.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore.Audio.Media
import android.widget.ImageButton
import android.widget.SeekBar
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.databinding.ActivityPlayMusicBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource

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
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            musicService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Service 연결
        val intent = Intent(this, MusicService::class.java)
        startService(intent)
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)

        //initializeLayout()

        // 재생/정지 버튼 이벤트
        binding.playBtn.setOnClickListener{
            if(musicService!!.isPlaying()) {
                musicService!!.pauseMusic()
            }
            else {
                musicService!!.playMusic()
            }
        }
        binding.previousBtnPA.setOnClickListener{prevNextSong(increment = false)}
        binding.nextBtnPA.setOnClickListener{prevNextSong(increment = true)}


        binding.seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                musicService!!.seekTo(p0!!.progress)
            }
        }
        )
    }
    private fun setLayout(){
        /*Glide.with(this)
            .load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_baseline_play_arrow_24).centerCrop())
            .into(binding.songImg)
        */

        binding.songNamePA.text = musicService!!.getTitle()
    }

    /*
    private fun initializeLayout(){
        songPosition = intent.getIntExtra("index", 0)
        when(intent.getStringExtra("class")){
            "MusicAdapter" ->{
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                setLayout()
            }
        }
    }
    */

    private fun prevNextSong(increment:Boolean){
        if(increment){
            //setSongPosition(increment = true)
            setLayout()
        }
        else{
            //setSongPosition(increment = false)
            setLayout()
        }
    }
}