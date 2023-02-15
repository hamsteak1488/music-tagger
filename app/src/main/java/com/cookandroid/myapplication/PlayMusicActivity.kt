package com.cookandroid.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.widget.ImageButton
import android.widget.SeekBar
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.databinding.ActivityPlayMusicBinding

class PlayMusicActivity : AppCompatActivity(), ServiceConnection {

    companion object{
        lateinit var musicListPA : ArrayList<Music>
        var songPosition : Int = 0
        var isPlaying:Boolean = false
        var musicService:MusicService? = null
        lateinit var binding: ActivityPlayMusicBinding
        var repeat: Boolean = false
        var nowPlayingId: String =""
        var fIndex: Int = -1
    }

    private lateinit var binding:ActivityPlayMusicBinding

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