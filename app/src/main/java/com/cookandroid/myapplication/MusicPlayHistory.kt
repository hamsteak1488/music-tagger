package com.cookandroid.myapplication

import android.util.Log

class MusicPlayHistory {

    private var playHistoryMap = HashMap<CharSequence, PlaytimeEachEnvironment>()

    fun addPlaytime(name:CharSequence, playtime:Long) {
        if (!playHistoryMap.containsKey(name)) {
            playHistoryMap[name] = PlaytimeEachEnvironment()
        }

        playHistoryMap[name]!!.addPlaytime(playtime)

        Log.d("myTag", "추가된 결과 : " + playHistoryMap[name])
    }
}