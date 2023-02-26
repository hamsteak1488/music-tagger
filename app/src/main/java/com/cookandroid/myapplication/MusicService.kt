package com.cookandroid.myapplication

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.BitmapCallback
import com.google.android.exoplayer2.ui.PlayerNotificationManager.MediaDescriptionAdapter
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

class MusicService : Service() {

    init {

    }

    private var myBinder = MyBinder()

    private var exoPlayer : ExoPlayer? = null
    private var playerNotificationManager : PlayerNotificationManager? = null

    private var playListMediaItem:ArrayList<MediaItem>? = null
    private var playListTitle:ArrayList<String>? = null
    private var currentMusic:Music? = null

    private var musicStartTime:Long = -1
    private lateinit var musicPlayHistory:MusicPlayHistory

    private val baseUrlStr = "http://121.181.181.105:8080/"
    val testAudioUriStr = baseUrlStr + "media?title=test_audio1.mp3"
    val testVideoUriStr = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

    inner class MyBinder: Binder(){
        fun currentService():MusicService{
            return this@MusicService
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return myBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        super.onCreate()

        initPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()

        exoPlayer?.release()
    }

    fun getSearchResult(title:String) : ArrayList<Music> {
        return ArrayList<Music>()
    }

    inner class PlayerStateListener : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)

            if (isPlaying) {
                Log.d("myTag", "isPlaying changed to true")

                musicStartTime = System.currentTimeMillis()

                getMusicMetadata(playListTitle!![exoPlayer!!.currentMediaItemIndex]) {
                    currentMusic = it
                }
            }
            else {
                Log.d("myTag", "isPlaying changed to false")

                if (musicStartTime != (-1).toLong()) {
                    val playedTime = System.currentTimeMillis() - musicStartTime
                    musicPlayHistory.addPlaytime(currentMusic!!.title, playedTime)
                    // musicPlayHistory.addPlaytime("star walkin", playedTime)
                    musicStartTime = -1
                }
            }
        }
    }

    // exoPlayer, 알림창 초기화
    private fun initPlayer() {
        //exoPlayer 초기화
        exoPlayer = ExoPlayer.Builder(applicationContext).build()
        //exoPlayer에 플레이리스트 지정할 것
        musicPlayHistory = MusicPlayHistory()
        exoPlayer!!.addListener(PlayerStateListener())


        createNotificationChannel()

        playerNotificationManager = PlayerNotificationManager.Builder(
            applicationContext,
            getString(R.string.NOTIFICATION_ID).toInt(),
            getString(R.string.CHANNEL_ID)
        )
            .setNotificationListener(MyNotificationListener())
            .setMediaDescriptionAdapter(MyMediaDescriptionAdapter())
            .build()

        playerNotificationManager!!.setPlayer(exoPlayer)
        playerNotificationManager!!.setPriority(NotificationCompat.PRIORITY_MAX)
        playerNotificationManager!!.setUseStopAction(true)
    }



    // 음악 재생
    public fun playMusic() {
        exoPlayer!!.prepare()
        exoPlayer!!.play()
    }

    // 음악 정지
    public fun pauseMusic() {
        if (exoPlayer!!.isPlaying) {
            exoPlayer!!.stop()
        }
    }

    // 음악 재생 여부
    public fun isPlaying() : Boolean {
        return exoPlayer!!.isPlaying
    }

    // 음악 제목 얻기
    public fun getTitle() : CharSequence {
        return currentMusic!!.title
    }

    // 재생 지점 이동
    public fun seekTo(progress:Int) {
        // progress는 0~100 사이의 값, 음악 길이를 100으로 나눠서 progress만큼 곱해준다
        exoPlayer!!.seekTo(exoPlayer!!.duration / 100 * progress)
    }

    // 재생 지점 반환 (0 ~ 100)
    public fun getProgress(): Int {
        val p = (exoPlayer!!.currentPosition / exoPlayer!!.duration * 100).toInt()
        return p
    }

    // 플레이리스트를 exoPlayer에 지정
    fun setPlayList(list: ArrayList<String>) {
        playListTitle = list
        playListMediaItem = ArrayList<MediaItem>().apply {
            playListTitle!!.forEach { title ->
                this.add(MediaItem.fromUri(baseUrlStr + "media?title=" + title + ".mp3"))
            }
        }
        exoPlayer!!.setMediaItems(playListMediaItem!!)
    }

    fun setPlayerView(view:PlayerControlView) {

        view.player = exoPlayer
    }

    interface RetrofitAPI {
        @GET("/metadata")
        fun getMetadata(@Query("title") title:String) : Call<Music>
    }

    fun getMusicMetadata(title:String, operation:(Music?)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrlStr)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)
        val callGetMetadata = api.getMetadata(title)
        callGetMetadata.enqueue(object:Callback<Music> {
            override fun onResponse(call: Call<Music>, response: Response<Music>) {
                Log.d("myTag", "success : ${response.raw()}")
                val result = response.body()
                Log.d("myTag", result.toString())
                operation(response.body())
            }

            override fun onFailure(call: Call<Music>, t: Throwable) {
                Log.d("myTag", "failure : $t")
            }
        })
    }


    // 알림창을 띄우기 위해 채널 생성
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Music Tagger Channel name"
            val description = "Music Tagger Channel description"
            val channel = NotificationChannel(
                getString(R.string.CHANNEL_ID),
                name,
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    // 알림창 이벤트 관리
    private inner class MyNotificationListener : PlayerNotificationManager.NotificationListener {
        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            super.onNotificationCancelled(notificationId, dismissedByUser)
        }

        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
            super.onNotificationPosted(notificationId, notification, ongoing)
        }
    }

    // 알림창 정보 표시 관리
    private inner class MyMediaDescriptionAdapter : MediaDescriptionAdapter {
        override fun getCurrentContentTitle(player: Player): CharSequence {
            //return player.currentMediaItem?.mediaMetadata?.title!!
            return "MyMediaDescriptionAdapter.getCurrentContentTitle()"
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            val intent = Intent(applicationContext, MainActivity::class.java)
            return PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        override fun getCurrentContentText(player: Player): CharSequence? {
            return "myExoPlayerIntent"
        }

        override fun getCurrentLargeIcon(player: Player, callback: BitmapCallback): Bitmap? {
            return null
        }
    }
}