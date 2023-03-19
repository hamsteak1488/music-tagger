package com.cookandroid.myapplication

import android.app.*
import android.content.Intent
import android.graphics.Bitmap
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.BitmapCallback
import com.google.android.exoplayer2.ui.PlayerNotificationManager.MediaDescriptionAdapter
import org.json.JSONObject
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.*

class MusicService : Service() {

    init {

    }

    private var myBinder = MyBinder()

    /** MusicService의 ExoPlayer 인스턴스 */
    private var exoPlayer : ExoPlayer? = null
    /** ExoPlayer의 상단 알림창 매니저 */
    private var playerNotificationManager : PlayerNotificationManager? = null

    /** id를 통해 만들어진 Uri로 Mediaitem으로 변경 후 저장할 리스트 */
    private var playListMediaItem:ArrayList<MediaItem>? = null

    /** 현재 재생중인 음악 플레이리스트 인덱스 */
    var currentListPos:Int = -1

    /** 현재 재생중인 음악 메타데이터 정보 */
    var currentMusic:Music? = null

    var currentMusicPos:Int = -1

    /** 음악 청취 기록을 위한 음악 재생시작 시간 정보 */
    private var musicStartTime:Long = -1

    /** 음악 청취 기록을 관리하는 객체 */
    private lateinit var playtimeHistory:PlaytimeHistory

    /** MusicTagger의 서버 baseUrl */
    private val baseUrlStr = "http://10.0.2.2:8080/"

    val testAudioUriStr = baseUrlStr + "media?title=test_audio1.mp3"
    val testVideoUriStr = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

    inner class MyBinder: Binder(){
        fun currentService():MusicService{
            return this@MusicService
        }
    }

    override fun onBind(intent: Intent?): IBinder {
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

    /** 서비스 시작시 진행되는 초기화 과정 */
    private fun initPlayer() {
        /** exoPlayer 초기화 */
        exoPlayer = ExoPlayer.Builder(applicationContext).build()
        /** 청취기록객체 초기화 */
        playtimeHistory = PlaytimeHistory()
        loadPlaytimeHistory("woals1488") { }
        /** 이벤트 리스너 지정 */
        exoPlayer!!.addListener(PlayerStateListener())

        /** 알림 채널 생성 */
        createNotificationChannel()

        /** 알림 창 초기화 */
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

    /** 재생 동작이 변경될 때마다 이벤트 처리 */
    inner class PlayerStateListener : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)

            /** 재생이 시작될 때 */
            if (isPlaying) {
                Log.d("myTag", "isPlaying changed to true")

                /** 재생시작시간 기록 */
                musicStartTime = System.currentTimeMillis()

                currentMusicPos = exoPlayer!!.currentMediaItemIndex

                /** currentMediaItemIndex를 통해 현재 재생중인 음악의 인덱스를 얻은 후, id를 통해 메타데이터 받아와서 currentMusic에 저장 */
                getMusicMetadata(PlaylistManager.allPlayList[currentListPos].musicList[exoPlayer!!.currentMediaItemIndex].id) {
                    currentMusic = it;
                }
            }
                
            /** 재생이 멈췄을 때 */
            else {
                Log.d("myTag", "isPlaying changed to false")

                /** 재생시작시간 정보를 통해 재생된시간 계산 후 기록 */
                if (musicStartTime != (-1).toLong()) {
                    val playedTime = System.currentTimeMillis() - musicStartTime
                    playtimeHistory.addPlaytime(currentMusic!!.id, playedTime)
                    musicStartTime = -1
                }

                savePlaytimeHistory("woals:1000", playtimeHistory.toJson()) {

                }
            }
        }
    }

    fun setMusicPos(pos:Int) {
        currentMusic = PlaylistManager.allPlayList[currentListPos].musicList[pos]
        currentMusicPos = pos
    }

    fun setPlayList(idList:List<Int>) {
        playListMediaItem = ArrayList<MediaItem>().apply {
            idList.forEach { id ->
                this.add(MediaItem.fromUri(baseUrlStr + "media?id=" + id))
            }
        }
        exoPlayer!!.setMediaItems(playListMediaItem!!)

        getMusicMetadata(idList[0]) {
            currentMusic = it
        }
    }

    /** exoPlayer 플레이리스트 리로드 */
    fun reloadPlayer() {
        /** 음악 id를 통해 MediaItem 리스트를 생성한 후, exoPlayer의 리스트로 설정 */
        playListMediaItem = ArrayList<MediaItem>().apply {
            PlaylistManager.allPlayList[currentListPos].musicList.forEach { music ->
                this.add(MediaItem.fromUri(baseUrlStr + "media?id=" + music.id))
            }
        }
        exoPlayer!!.setMediaItems(playListMediaItem!!)

        currentMusic = PlaylistManager.allPlayList[currentListPos].musicList[currentMusicPos]

        exoPlayer!!.seekTo(currentMusicPos, C.TIME_UNSET)
    }

    /** 플레이어뷰의 플레이어를 exoPlayer로 지정 */
    fun setPlayerView(view:PlayerControlView) {
        view.player = exoPlayer
    }

    /** http 통신을 위한 retrofit의 API 인터페이스 */
    interface RetrofitAPI {
        /** id를 통해 메타데이터에 접근, id는 유일성을 가지므로 반환형은 Music */
        @GET("/metadata")
        fun getMetadata(@Query("id") id:Int) : Call<Music>

        /** 메타데이터 항목의 내용으로 접근, 여러 결과가 나올 수 있으므로 반환형은 List<Music> */
        @GET("/metadatalist")
        fun getMetadataList(@Query("items") item:List<String>, @Query("name") name:String) : Call<List<Music>>

        /** id를 통해 ArtImage 요청 */
        @GET("/img")
        fun getArtImg(@Query("id") id:Int) : Call<Array<Byte>>

        /** 태그데이터 삽입 */
        @POST("/playtimehistory/insert")
        fun insertPlaytimeHistory(@Query("emailAndMusicId") emailAndMusicId:String, @Body tagInfo:JSONObject) : Call<String>
        
        /** 태그데이터 업데이트 */
        @POST("/playtimehistory/update")
        fun updatePlaytimeHistory(@Query("emailAndMusicId") emailAndMusicId:String, @Body tagInfo:JSONObject) : Call<String>

        /** 태그데이터 불러오기 */
        @POST("/playtimehistory/select")
        fun getPlaytimeHistory(@Query("emailAndMusicId") emailAndMusicId:String) : Call<JSONObject>
    }

    /** 호출 시 id를 통해 메타데이터를 서버에 요청, response가 오면 호출될 함수 operation을 인자로 넘겨주어야 함 */
    fun getMusicMetadata(id:Int, operation:(Music?)->Unit) {
        /** retrofit 객체 초기화 */
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrlStr)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)

        /** api를 통해 서버에 request를 보냄 */
        val callGetMetadata = api.getMetadata(id)
        callGetMetadata.enqueue(object:Callback<Music> {
            override fun onResponse(call: Call<Music>, response: Response<Music>) {
                /** response가 정상적으로 왔다면 operation 함수 호출 */
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

    /** 호출 시 항목이름과 항목내용을 통해 메타데이터를 서버에 요청, 호출될 함수 operation의 인자가 리스트형태 */
    fun getMusicMetadataList(items:List<String>, name:String, operation:(List<Music>?)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrlStr)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)
        val callGetMetadata = api.getMetadataList(items, name)
        callGetMetadata.enqueue(object:Callback<List<Music>> {
            override fun onResponse(call: Call<List<Music>>, response: Response<List<Music>>) {
                Log.d("myTag", "success : ${response.raw()}")
                val result = response.body()
                Log.d("myTag", result.toString())
                operation(response.body())
            }
            override fun onFailure(call: Call<List<Music>>, t: Throwable) {
                Log.d("myTag", "failure : $t")
            }
        })
    }

    /** 호출 시 항목이름과 항목내용을 통해 메타데이터를 서버에 요청, 호출될 함수 operation의 인자가 리스트형태 */
    fun getMusicArtImg(id:Int, operation:(Array<Byte>?)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrlStr)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)
        val callGetMetadata = api.getArtImg(id)
        callGetMetadata.enqueue(object:Callback<Array<Byte>> {
            override fun onResponse(call: Call<Array<Byte>>, response: Response<Array<Byte>>) {
                Log.d("myTag", "success : ${response.raw()}")
                val result = response.body()
                Log.d("myTag", result.toString())
                operation(response.body())
            }
            override fun onFailure(call: Call<Array<Byte>>, t: Throwable) {
                Log.d("myTag", "failure : $t")
            }
        })
    }

    fun loadPlaytimeHistory(emailAndMusicId: String, operation:(Boolean?)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrlStr)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)

        val callGetMetadata = api.getPlaytimeHistory(emailAndMusicId)
        callGetMetadata.enqueue(object:Callback<JSONObject> {
            override fun onResponse(call: Call<JSONObject>, response: Response<JSONObject>) {
                Log.d("myTag", "success : ${response.raw()}")
                val result = response.body()
                Log.d("myTag", result.toString())

                playtimeHistory.set(response.body()!!)
                operation(true)
            }
            override fun onFailure(call: Call<JSONObject>, t: Throwable) {
                Log.d("myTag", "failure : $t")
                operation(false)
            }
        })
    }
    //TODO : json 객체가 서버에 도착했을때 값이 이상하게 나오는 문제 수정할 것
    fun savePlaytimeHistory(emailAndMusicId: String, tagInfo: JSONObject, operation:(Boolean?)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrlStr)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)

        loadPlaytimeHistory(emailAndMusicId) {
            if (it!!) {
                val callGetMetadata = api.updatePlaytimeHistory(emailAndMusicId, tagInfo)
                callGetMetadata.enqueue(object:Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        Log.d("myTag", "success : ${response.raw()}")
                        val result = response.body()
                        Log.d("myTag", result.toString())
                        operation(true)
                    }
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.d("myTag", "failure : $t")
                    }
                })
            }
            else {
                val callGetMetadata = api.insertPlaytimeHistory(emailAndMusicId, tagInfo)
                callGetMetadata.enqueue(object:Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        Log.d("myTag", "success : ${response.raw()}")
                        val result = response.body()
                        Log.d("myTag", result.toString())
                        operation(false)
                    }
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.d("myTag", "failure : $t")
                    }
                })
            }
        }
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