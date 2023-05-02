package com.cookandroid.myapplication.activities

import android.os.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.MusicService
import com.cookandroid.myapplication.MusicServiceConnection
import com.cookandroid.myapplication.PlaylistManager
import com.cookandroid.myapplication.R
import com.cookandroid.myapplication.databinding.ActivityPlayMusicBinding

class PlayMusicActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayMusicBinding
    private val mService = MusicServiceConnection.musicService!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtnPA.setOnClickListener { finish() }
        // 이모지사진 변경
//        timeEmoji()
//        weatherEmoji()
//        seasonEmoji()
//        weekdayEmoji()
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
    }

    override fun onResume() {
        super.onResume()


    }

    override fun onDestroy() {
        super.onDestroy()

        mService.removeMediaItemChangeListenerForPlayMusicActivity()
    }

    private fun setLayout() {
        mService.getMusicMetadata(PlaylistManager.playlists[mService.currentListPos].musicList[mService.currentMusicPos]) {
            if (it == null) return@getMusicMetadata
            binding.songNamePA.text = it.title
            binding.songNamePA.invalidate()

            Glide.with(this@PlayMusicActivity)
                .load("http://10.0.2.2:8080/img?id=" + (it.id))
                .apply(RequestOptions().placeholder(R.drawable.ic_baseline_music_note_24).centerCrop())
                .into(binding.songImg)
            binding.songImg.invalidate()
        }
    }
    /*
// todo("data 변경")
    private fun timeEmoji(){
    when(data){
        "새벽", "이른 아침", "늦은 아침" -> binding.timeEmoji.setImageResource(R.drawable.morining)
        "이른 오후", "늦은 오후" -> binding.timeEmoji.setImageResource(R.drawable.afternoon)
        "저녁"->binding.timeEmoji.setImageResource(R.drawable.evening)
        "밤"->binding.timeEmoji.setImageResource(R.drawable.night)
    }
    }
    private fun weatherEmoji(){
        when(data){
            "맑음" -> binding.weatherEmoji.setImageResource(R.drawable.sun)
            "구름", "구름 흩뿌려짐", "구름 많이 낌","안개" ->binding.weatherEmoji.setImageResource(R.drawable.cloudy)
            "소나기", "비"-> binding.weatherEmoji.setImageResource(R.drawable.rain)
            "천둥" -> binding.weatherEmoji.setImageResource(R.drawable.thunder)
            "눈"->binding.weatherEmoji.setImageResource(R.drawable.snow)
            "예외 날씨" ->binding.weatherEmoji.setImageResource(R.drawable.exept)
        }

    }
    private fun seasonEmoji(){
        when(data){
            "봄" -> binding.weatherEmoji.setImageResource(R.drawable.springtag)
            "여름"->binding.weatherEmoji.setImageResource(R.drawable.summer)
            "가을" ->binding.weatherEmoji.setImageResource(R.drawable.fall)
            "겨울" ->binding.weatherEmoji.setImageResource(R.drawable.winter)

        }
    }
    private fun weekdayEmoji(){
        when(data){
            "월"->binding.weekdayEmoji.text="Mon"
            "화"->binding.weekdayEmoji.text="Tue"
            "수"->binding.weekdayEmoji.text="wed"
            "목"->binding.weekdayEmoji.text="Thu"
            "금"->binding.weekdayEmoji.text="Fri"
            "토"->binding.weekdayEmoji.text="Sat"
            "일"->binding.weekdayEmoji.text="Sun"
        }
    }
*/
}