package com.cookandroid.myapplication

import android.util.Log
import com.cookandroid.myapplication.util.SurroundingsManager
import com.google.gson.JsonObject
import com.tftf.util.PlayHistory

class PlayHistoryManager {

    // todo : 특정 태그들만 선별해서 서버에 추천리스트 요청

    private var musicPlayHistory = HashMap<Int, PlayHistory>()

    //
    fun addPlaytime(id:Int, playtime:Long) {
        if (!musicPlayHistory.containsKey(id)) {
            musicPlayHistory[id] = PlayHistory()
        }

        SurroundingsManager.getCurrentSurroundings { s ->
            musicPlayHistory[id]!!.addPlaytime(s, playtime)
        }

        Log.d("myTag", "추가된 결과 : " + musicPlayHistory[id]!!.totalPlaytime)
    }

    fun set(musicId:Int, jo: JsonObject) {
        if(!musicPlayHistory.containsKey(musicId)) {
            musicPlayHistory[musicId] = PlayHistory()
        }
        musicPlayHistory[musicId]!!.importFromJson(jo)
    }

    fun toJson(musicId: Int) : JsonObject {
        return musicPlayHistory[musicId]!!.exportToJson()
    }
}