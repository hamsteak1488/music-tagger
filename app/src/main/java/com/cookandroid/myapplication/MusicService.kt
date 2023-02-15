package com.cookandroid.myapplication

import android.app.*
import android.content.Intent
import android.graphics.Bitmap
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.BitmapCallback
import com.google.android.exoplayer2.ui.PlayerNotificationManager.MediaDescriptionAdapter
import java.util.*

class MusicService: Service() {
    private var myBinder = MyBinder()

    private var exoPlayer : ExoPlayer? = null
    private var playerNotificationManager : PlayerNotificationManager? = null

    private var playList:List<MediaItem>? = null;

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

    // exoPlayer, 알림창 초기화
    private fun initPlayer() {
        //exoPlayer 초기화
        exoPlayer = ExoPlayer.Builder(applicationContext).build()
        //exoPlayer에 플레이리스트 지정

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
        //return exoPlayer!!.mediaMetadata!!.title!!
        return "getTitle"
    }

    // 재생 지점 이동
    public fun seekTo(progress:Int) {
        // progress는 0~100 사이의 값, 음악 길이를 100으로 나눠서 progress만큼 곱해준다
        val pos:Long = exoPlayer!!.duration / 100 * progress
        exoPlayer!!.seekTo(pos)
    }

    // 플레이리스트를 exoPlayer에 지정
    public fun setPlayList(list: ArrayList<MediaItem>) {
        playList = list
        exoPlayer!!.setMediaItems(playList!!)
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