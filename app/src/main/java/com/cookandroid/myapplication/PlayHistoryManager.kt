package com.cookandroid.myapplication

import android.util.Log
import com.google.gson.JsonObject
import com.tftf.util.PlayHistory

class PlayHistoryManager {

    // todo : 특정 태그들만 선별해서 서버에 추천리스트 요청

    private var musicPlayHistory = HashMap<Int, PlayHistory>()

    //
    fun addPlaytime(id:Int, playtime:Long, callbackOperation:()->Unit) {
        if (!musicPlayHistory.containsKey(id)) {
            musicPlayHistory[id] = PlayHistory()
        }

        SurroundingsManager.getCurrentSurroundings { s ->
            musicPlayHistory[id]!!.addPlaytime(s, playtime)
            callbackOperation()
        }

        Log.d("myTag", "추가된 결과 : " + musicPlayHistory[id]!!.totalPlaytime)
    }

    fun importFromJson(musicId:Int, jo: JsonObject){
        if(!musicPlayHistory.containsKey(musicId)) {
            musicPlayHistory.put(musicId, PlayHistory())
        }
        musicPlayHistory[musicId]!!.importFromJson(jo)
    }

    fun exportToJson(musicId: Int) : JsonObject {
        return musicPlayHistory[musicId]!!.exportToJson()
    }
}