package com.cookandroid.myapplication

import android.util.Log
import com.google.gson.JsonObject

class PlaytimeHistory {

    private var playtimeHistoryMap = HashMap<Int, PlaytimeEachEnvironment>()

    //
    fun addPlaytime(id:Int, playtime:Long) {
        if (!playtimeHistoryMap.containsKey(id)) {
            playtimeHistoryMap[id] = PlaytimeEachEnvironment()
        }

        playtimeHistoryMap[id]!!.addPlaytime(playtime)

        Log.d("myTag", "추가된 결과 : " + playtimeHistoryMap[id]!!.totalPlaytime)
    }

    fun set(musicId:Int, jo: JsonObject) {
        if(!playtimeHistoryMap.containsKey(musicId)) {
            playtimeHistoryMap[musicId] = PlaytimeEachEnvironment()
        }
        playtimeHistoryMap[musicId]!!.set(jo)
    }

    fun toJson(musicId: Int) : JsonObject {
        return playtimeHistoryMap[musicId]!!.toJson()
    }
}