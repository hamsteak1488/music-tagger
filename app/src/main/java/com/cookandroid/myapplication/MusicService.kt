package com.cookandroid.myapplication

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder

class MusicService: Service() {
    private var myBinder = MyBinder()
    var mediaPlayer:MediaPlayer? = null
    private lateinit var runnable: Runnable
    lateinit var  audioManager: AudioManager

    override fun onBind(intent: Intent?): IBinder? {
         return myBinder
    }
    inner class MyBinder: Binder(){
        fun currentService():MusicService{
            return this@MusicService
        }
    }
}