package com.cookandroid.myapplication

import android.app.*
import android.content.Intent
import android.graphics.Bitmap
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.cookandroid.myapplication.activities.MainActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.MediaItem.fromUri
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.BitmapCallback
import com.google.android.exoplayer2.ui.PlayerNotificationManager.MediaDescriptionAdapter
import com.google.gson.JsonObject
import com.tftf.util.Music
import com.tftf.util.Playlist
import com.tftf.util.PlaylistManagerDTO
import com.tftf.util.PlaytimeHistoryDTO
import com.tftf.util.Surroundings
import okhttp3.ResponseBody
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList


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

    var currentMusicPos:Int = -1

    /** 음악 청취 기록을 위한 음악 재생시작 시간 정보 */
    private var musicStartTime:Long = -1

    interface OnMediaItemChangeListener {
        fun onMediaItemChange()
    }
    private var mediaItemChangeListenerForPlayMusicActivity : OnMediaItemChangeListener? = null

    var email:String = ""

    /** MusicTagger의 서버 baseUrl */
    private val baseUrlStr = "http://10.0.2.2:8080/"

    val testAudioUriStr = baseUrlStr + "media?title=test_audio1.mp3"
    val testVideoUriStr = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

    inner class MyBinder: Binder(){
        fun currentService():MusicService{
            return this@MusicService
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //todo:email = intent!!.getStringExtra("email")!!
        initPlayer()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder {
        return myBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()

        exoPlayer?.release()
    }


    fun loadData() {
        loadPlaytimeHistoryList() { dtoList ->
            if (dtoList != null) {
                for (dto in dtoList) {
                    PlayHistoryManager.importFromJson(dto.musicId, dto.historyJO)
                }
            }
        }

        loadPlaylistManager() {
            if (it != null) {
                PlaylistManager.set(it)

                PlaylistManager.initTempPlaylist()
            }
        }
    }

    /** 서비스 시작시 진행되는 초기화 과정 */
    private fun initPlayer() {
        /** exoPlayer 초기화 */
        exoPlayer = ExoPlayer.Builder(applicationContext).build()

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

            if (currentListPos == -1 || currentMusicPos == -1) return

            /** 재생이 시작될 때 */
            if (isPlaying) {
                Log.d("myTag", "isPlaying changed to true")

                /** 재생시작시간 기록 */
                musicStartTime = System.currentTimeMillis()

                currentMusicPos = exoPlayer!!.currentMediaItemIndex
            }
                
            /** 재생이 멈췄을 때 */
            else {
                Log.d("myTag", "isPlaying changed to false")

                val curMusicId = PlaylistManager.playlists[currentListPos].musicList[currentMusicPos]

                /** 재생시작시간 정보를 통해 재생된시간 계산 후 기록 */
                if (musicStartTime != (-1).toLong()) {
                    val playedTime = System.currentTimeMillis() - musicStartTime
                    PlayHistoryManager.addPlaytime(curMusicId, playedTime) {
                        savePlaytimeHistory(PlaytimeHistoryDTO(email, curMusicId, PlayHistoryManager.exportToJson(curMusicId))) { }
                    }
                    musicStartTime = -1
                }
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)

            mediaItem?.apply {
                currentMusicPos = mediaId.toInt()
            }

            mediaItemChangeListenerForPlayMusicActivity?.onMediaItemChange()
        }
    }

    fun setMediaItemChangeListenerForPlayMusicActivity(listener:OnMediaItemChangeListener) {
        mediaItemChangeListenerForPlayMusicActivity = listener
    }

    fun removeMediaItemChangeListenerForPlayMusicActivity() {
        mediaItemChangeListenerForPlayMusicActivity = null
    }

    fun prepareAndPlay() {
        exoPlayer!!.prepare()
        exoPlayer!!.play()
    }

    fun setMusicPos(pos:Int) {
        currentMusicPos = pos
    }

    fun setMediaList(idList:List<Int>) {
        playListMediaItem = ArrayList<MediaItem>().apply {
            idList.forEach { id ->
                this.add(MediaItem.fromUri(baseUrlStr + "media?id=" + id))
            }
        }
        exoPlayer!!.setMediaItems(playListMediaItem!!)
    }

    fun hasCurrentMediaItem() : Boolean {
        return exoPlayer!!.currentMediaItem != null
    }

    /** exoPlayer 플레이리스트 리로드 */
    fun reloadPlaylist() {
        /** 음악 id를 통해 MediaItem 리스트를 생성한 후, exoPlayer의 리스트로 설정 */
        playListMediaItem = ArrayList<MediaItem>().apply {
            PlaylistManager.playlists[currentListPos].musicList.forEachIndexed { pos, musicId ->
                this.add(MediaItem.Builder()
                    .setUri(baseUrlStr + "media?id=" + musicId)
                    .setMediaId(pos.toString())
                    .build())
            }
        }
    }

    fun reloadPlayer(listPos:Int, musicPos:Int) {
        currentListPos = listPos
        reloadPlaylist()
        exoPlayer!!.setMediaItems(playListMediaItem!!)

        currentMusicPos = musicPos
        exoPlayer!!.seekTo(currentMusicPos, C.TIME_UNSET)
    }

    /** 플레이어뷰의 플레이어를 exoPlayer로 지정 */
    fun setViewPlayer(view:PlayerControlView) {
        view.player = exoPlayer
    }

    class NullOnEmptyConverterFactory : Converter.Factory() {
        override fun responseBodyConverter(
            type: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
        ): Converter<ResponseBody, *> {
            val delegate: Converter<ResponseBody, *> =
                retrofit.nextResponseBodyConverter<Any>(this, type, annotations)
            return Converter { body ->
                if (body.contentLength() == 0L) null else delegate.convert(
                    body
                )
            }
        }
    }

    /** http 통신을 위한 retrofit의 API 인터페이스 */
    interface RetrofitAPI {
        /** id를 통해 메타데이터에 접근, id는 유일성을 가지므로 반환형은 Music */
        @GET("/metadata")
        fun getMetadata(@Query("id") id:Int) : Call<Music>

        @GET("/metadatalist")
        fun getMetadataList(@Query("ids") ids:List<Int>) : Call<List<Music>>

        /** 메타데이터 항목의 내용으로 접근, 여러 결과가 나올 수 있으므로 반환형은 List<Music> */
        @GET("/metadatalist")
        fun getMetadataList(@Query("items") item:List<String>, @Query("name") name:String) : Call<List<Music>>


        /** id를 통해 ArtImage 요청 */
        @GET("/img")
        fun getArtImg(@Query("id") id:Int) : Call<Array<Byte>>


        /** 태그데이터 저장 */
        @POST("/playtimehistory/save")
        fun savePlaytimeHistory(@Body historyDTO: PlaytimeHistoryDTO) : Call<String>
        /** 태그데이터 불러오기 */
        @POST("/playtimehistory/select")
        fun selectPlaytimeHistory(@Query("email") email:String, @Query("musicId") musicId:Int) : Call<PlaytimeHistoryDTO>

        @POST("/playtimehistory/select")
        fun selectPlaytimeHistoryList(@Query("email") email: String) : Call<List<PlaytimeHistoryDTO>>


        @POST("/playlist/save")
        fun savePlaylistManager(@Body playlistManagerDTO: PlaylistManagerDTO) : Call<String>

        @POST("/playlist/select")
        fun selectPlaylistManager(@Query("email") email: String) : Call<PlaylistManagerDTO>


        @POST("/recommend/personalized")
        fun getPersonalizedList(@Query("email") email: String,
                                @Body surroundings: Surroundings,
                                @Query("listSize") listSize: Int) : Call<List<Int>>

        @POST("/recommend/theme")
        fun getThemeList(@Body surroundings: Surroundings, @Query("listSize") listSize:Int) : Call<List<Playlist>>
    }

    /** 호출 시 id를 통해 메타데이터를 서버에 요청, response가 오면 호출될 함수 operation을 인자로 넘겨주어야 함 */
    fun getMusicMetadata(id:Int, operation:(Music?)->Unit) {
        /** retrofit 객체 초기화 */
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrlStr)
            .addConverterFactory(NullOnEmptyConverterFactory())
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

    fun getMusicMetadataList(ids:List<Int>, operation: (List<Music>?) -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrlStr)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)
        val callGetMetadata = api.getMetadataList(ids)
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

    fun loadPlaytimeHistory(musicId: Int, operation:(PlaytimeHistoryDTO?)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrlStr)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)

        val callGetMetadata = api.selectPlaytimeHistory(email, musicId)
        callGetMetadata.enqueue(object:Callback<PlaytimeHistoryDTO> {
            override fun onResponse(call: Call<PlaytimeHistoryDTO>, response: Response<PlaytimeHistoryDTO>) {
                Log.d("myTag", "success : ${response.raw()}")
                Log.d("myTag", response.body().toString())
                operation(response.body())
            }
            override fun onFailure(call: Call<PlaytimeHistoryDTO>, t: Throwable) {
                Log.d("myTag", "failure : $t")
            }
        })
    }

    fun loadPlaytimeHistoryList(operation:(List<PlaytimeHistoryDTO>?)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrlStr)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)

        val callGetMetadata = api.selectPlaytimeHistoryList(email)
        callGetMetadata.enqueue(object:Callback<List<PlaytimeHistoryDTO>> {
            override fun onResponse(call: Call<List<PlaytimeHistoryDTO>>, response: Response<List<PlaytimeHistoryDTO>>) {
                Log.d("myTag", "success : ${response.raw()}")
                Log.d("myTag", response.body().toString())
                operation(response.body())
            }
            override fun onFailure(call: Call<List<PlaytimeHistoryDTO>>, t: Throwable) {
                Log.d("myTag", "failure : $t")
            }
        })
    }

    fun savePlaytimeHistory(dto:PlaytimeHistoryDTO, operation:(Boolean?)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrlStr)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)

        val callGetMetadata = api.savePlaytimeHistory(dto)
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



    fun loadPlaylistManager(operation:(PlaylistManagerDTO?)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrlStr)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)

        val callGetMetadata = api.selectPlaylistManager(email)
        callGetMetadata.enqueue(object:Callback<PlaylistManagerDTO> {
            override fun onResponse(call: Call<PlaylistManagerDTO>, response: Response<PlaylistManagerDTO>) {
                Log.d("myTag", "success : ${response.raw()}")
                Log.d("myTag", response.body().toString())
                operation(response.body())
            }
            override fun onFailure(call: Call<PlaylistManagerDTO>, t: Throwable) {
                Log.d("myTag", "failure : $t")
            }
        })
    }

    fun savePlaylistManager(operation:(Boolean?)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrlStr)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)

        val callGetMetadata = api.savePlaylistManager(PlaylistManager.toDto(email))
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


    fun getPersonalizedList(surroundings: Surroundings, listSize: Int = 20, operation:(List<Int>?)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrlStr)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)
        val callGetMetadata = api.getPersonalizedList(email, surroundings, listSize)
        callGetMetadata.enqueue(object:Callback<List<Int>> {
            override fun onResponse(call: Call<List<Int>>, response: Response<List<Int>>) {
                Log.d("myTag", "success : ${response.raw()}")
                val result = response.body()
                Log.d("myTag", result.toString())
                operation(response.body())
            }
            override fun onFailure(call: Call<List<Int>>, t: Throwable) {
                Log.d("myTag", "failure : $t")
            }
        })
    }

    fun getThemeList(surroundings: Surroundings, listSize: Int = 20, operation:(List<Playlist>?)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrlStr)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)
        val callGetMetadata = api.getThemeList(surroundings, listSize)
        callGetMetadata.enqueue(object:Callback<List<Playlist>> {
            override fun onResponse(call: Call<List<Playlist>>, response: Response<List<Playlist>>) {
                Log.d("myTag", "success : ${response.raw()}")
                val result = response.body()
                Log.d("myTag", result.toString())
                operation(response.body())
            }
            override fun onFailure(call: Call<List<Playlist>>, t: Throwable) {
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