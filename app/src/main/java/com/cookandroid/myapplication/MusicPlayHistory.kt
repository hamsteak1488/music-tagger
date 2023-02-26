package com.cookandroid.myapplication

import android.util.Log

class MusicPlayHistory {

    private var playHistoryMap = HashMap<CharSequence, PlaytimeEachEnvironment>()

    //
    fun addPlaytime(title:CharSequence, playtime:Long) {
        if (!playHistoryMap.containsKey(title)) {
            playHistoryMap[title] = PlaytimeEachEnvironment()
        }

        playHistoryMap[title]!!.addPlaytime(playtime)

        Log.d("myTag", "추가된 결과 : " + playHistoryMap[title]!!.totalPlaytime)
    }
}