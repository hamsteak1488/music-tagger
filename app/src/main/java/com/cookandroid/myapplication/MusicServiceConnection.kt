package com.cookandroid.myapplication

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder

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