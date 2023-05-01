package com.cookandroid.myapplication

import android.util.Log
import com.google.gson.JsonObject
import com.tftf.util.MusicTag
import com.tftf.util.MusicTagger
import com.tftf.util.PlayHistory

object PlayHistoryManager {

    private var musicPlayHistory = HashMap<Int, PlayHistory>()


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


    fun getMusicTag(id:Int) : MusicTag? {
        return when (musicPlayHistory.containsKey(id)) {
            true -> MusicTagger.getMusicTag(musicPlayHistory[id])
            false -> null
        }
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