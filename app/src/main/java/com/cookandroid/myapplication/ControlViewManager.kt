package com.cookandroid.myapplication

import android.view.View
import com.google.android.exoplayer2.ui.PlayerControlView

object ControlViewManager {

    fun displayControlView(view:PlayerControlView) {
        val mService = MusicServiceConnection.musicService

        if (mService?.hasCurrentMediaItem() == true) {
            view.visibility = View.VISIBLE
            mService.setViewPlayer(view)
        }
        else {
            view.visibility = View.INVISIBLE
        }
    }
}