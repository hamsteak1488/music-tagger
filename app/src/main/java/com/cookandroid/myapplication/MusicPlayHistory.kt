package com.cookandroid.myapplication

import android.util.Log

class MusicPlayHistory {

    private var playHistoryMap = HashMap<Int, PlaytimeEachEnvironment>()

    //
    fun addPlaytime(id:Int, playtime:Long) {
        if (!playHistoryMap.containsKey(id)) {
            playHistoryMap[id] = PlaytimeEachEnvironment()
        }

        playHistoryMap[id]!!.addPlaytime(playtime)

        Log.d("myTag", "추가된 결과 : " + playHistoryMap[id]!!.totalPlaytime)
    }
}