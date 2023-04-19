package com.cookandroid.myapplication

import android.util.Log
import com.google.gson.JsonObject

class PlayHistoryManager {

    // todo : 특정 태그들만 선별해서 서버에 추천리스트 요청

    private var musicPlayHistory = HashMap<Int, PlayHistory>()

    //
    fun addPlaytime(id:Int, playtime:Long) {
        if (!musicPlayHistory.containsKey(id)) {
            musicPlayHistory[id] = PlayHistory()
        }

        musicPlayHistory[id]!!.addPlaytime(playtime)

        Log.d("myTag", "추가된 결과 : " + musicPlayHistory[id]!!.totalPlaytime)
    }

    fun set(musicId:Int, jo: JsonObject) {
        if(!musicPlayHistory.containsKey(musicId)) {
            musicPlayHistory[musicId] = PlayHistory()
        }
        musicPlayHistory[musicId]!!.set(jo)
    }

    fun toJson(musicId: Int) : JsonObject {
        return musicPlayHistory[musicId]!!.toJson()
    }
}