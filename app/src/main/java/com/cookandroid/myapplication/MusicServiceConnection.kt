package com.cookandroid.myapplication

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import com.cookandroid.myapplication.activities.Loading

object MusicServiceConnection : ServiceConnection {

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

/*
class MusicServiceConnection private constructor() : ServiceConnection {

    companion object {
        private var instance:MusicServiceConnection? = null
        var musicService:MusicService? = null
        private lateinit var context: Context

        fun getInstance(_context:Context):MusicServiceConnection {
            return instance?: synchronized(this) {
                instance?:MusicServiceConnection().also {
                    context = _context
                    instance = it
                }
            }
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val serviceBinder = service as MusicService.MyBinder
        musicService = serviceBinder.currentService()
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }
}
*/