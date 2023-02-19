package com.cookandroid.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.ImageButton
import com.cookandroid.myapplication.databinding.ActivityPlayMusicBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView

class PlayMusicActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayMusicBinding

    var exoPlayer: ExoPlayer? = null
    var isBound = false //서비스 바운드 여부
    var baseUriString = "http://10.0.2.2:8080/media"
    var bunnyUri =
        Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
    var fileUri = "file://disk/download/mp3/mymusic.mp3"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("myTag", "MainActivity onCreate")
        setContentView(R.layout.activity_play_music)
        initService() // 서비스 초기화 및 연결
        val button_play = findViewById<ImageButton>(R.id.playBtn)
        //val editText_mediaFileName = findViewById<EditText>(R.id.editText_mediaFileName)

        //뒤로 가기 버튼
        binding.backBtnPA.setOnClickListener { finish() }

        button_play.setOnClickListener {
            val mediaUri: Uri
            /*
            mediaUri =
                if (editText_mediaFileName.text.toString()
                        .contentEquals("")
                ) bunnyUri else Uri.parse(baseUriString + "?name=" + editText_mediaFileName.text.toString())
            */

            //exoPlayer에 뷰와 미디어아이템 연결
            val styledPlayerView = findViewById<StyledPlayerView>(R.id.exoPlayerView)
            styledPlayerView.player = exoPlayer
            val mediaItem = MediaItem.fromUri(bunnyUri)
            exoPlayer!!.setMediaItem(mediaItem)
            exoPlayer!!.prepare()
            exoPlayer!!.play()

        }
        //val button_finish = findViewById<Button>(R.id.button_finish)
        //button_finish.setOnClickListener { finish() }

    }

    // 서비스 연결과 해제시 호출될 이벤트를 미리 정의
    var serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val serviceBinder: MusicService.MyBinder =
                service as MusicService.MyBinder
            exoPlayer = serviceBinder.currentService().exoPlayer
            isBound = true
            Log.d("myTag", "MainActivity onServiceConnected")
        }

        override fun onServiceDisconnected(name: ComponentName) {
            exoPlayer = null
            isBound = false
            Log.d("myTag", "MainActivity onServiceDisconnected")
        }
    }

    // 서비스 시작과 바인드
    private fun initService() {
        Log.d("myTag", "MainActivity initService")
        val serviceIntent = Intent(this@PlayMusicActivity, MusicService::class.java)
        startService(serviceIntent)
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(serviceConnection)
        }
        Log.d("myTag", "MainActivity onDestroy")
    }


}

/*
class PlayMusicActivity : AppCompatActivity(), ServiceConnection {

    companion object{
        lateinit var musicListPA : ArrayList<Music>
        var songPosition : Int = 0
        var isPlaying:Boolean = false
        var musicService:MusicService? = null
        lateinit var binding: ActivityPlayMusicBinding
        var repeat: Boolean = false
        var nowPlayingId:String =""
        var fIndex: Int = -1
    }

    private lateinit var binding:ActivityPlayMusicBinding

    lateinit var playButton: ImageButton
    lateinit var seek_bar: SeekBar
    lateinit var runnable:Runnable
    private var handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Service 연결
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)

        initializeLayout()
        binding.playBtn.setOnClickListener{
            if(isPlaying) pauseMusic()
            else playMusic()
        }
        binding.previousBtnPA.setOnClickListener{prevNextSong(increment = false)}
        binding.nextBtnPA.setOnClickListener{prevNextSong(increment = true)}
    }
    private fun setLayout(){
        Glide.with(this)
            .load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_baseline_play_arrow_24).centerCrop())
            .into(binding.songImg)
        binding.songNamePA.text = musicListPA[songPosition].title
    }


    private fun createMediaPlayer() {
        try {
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying = true
            ///binding.playBtn.setIconResource(R.drawable.ic_baseline_pause_24)
        } catch (e: Exception) {return}
    }
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
    private fun playMusic(){
        ///binding.playBtn.setIconResource(R.drawable.ic_baseline_pause_24)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
    }
    private fun pauseMusic(){
        ///binding.playBtn.setIconResource(R.drawable.ic_baseline_play_arrow_24)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
    }
    private fun prevNextSong(increment:Boolean){
        if(increment){
            setSongPosition(increment = true)
            setLayout()
        }
        else{
            setSongPosition(increment = false)
            setLayout()
        }
    }
    private fun setSongPosition(increment:Boolean){
        if(increment)
        {
            if(musicListPA.size - 1 == songPosition)
                songPosition = 0
            else ++songPosition
        }
        else{
            if(songPosition == 0)
                songPosition = musicListPA.size-1
            else -- songPosition
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaPlayer()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }
}
*/