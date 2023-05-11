package com.cookandroid.myapplication

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder

object MusicServiceConnection : ServiceConnection {

    // ipconfig
    val serverUrl = "http://192.168.43.158:8080/"
    // val serverUrl = "http://10.0.2.2:8080/"

    var musicService:MusicService? = null

    private var callbackFunc: (() -> Unit)? = null

    @JvmName("setCallbackFunc1")
    fun setCallbackFunc(func: (() -> Unit)) {
        callbackFunc = func
    }

    fun getInstance(_context:Context):MusicServiceConnection {
        return this@MusicServiceConnection
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val serviceBinder = service as MusicService.MyBinder
        musicService = serviceBinder.currentService()

        callbackFunc?.let { it() }
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }
}