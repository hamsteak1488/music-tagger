package com.cookandroid.myapplication

import android.util.Log
import com.google.gson.JsonObject
import com.tftf.util.MusicTag
import com.tftf.util.MusicTagger
import com.tftf.util.PlayHistory

object PlayHistoryManager {


    fun cumulateHistory(musicID:Int, playedTime:Long, callbackOperation:()->Unit) {

        SurroundingsManager.getCurrentSurroundings { surroundings ->
            musicPlayHistory[id]!!.addPlaytime(surroundings, playtime)
            callbackOperation()
        }

        Log.d("myTag", "추가된 결과 : " + musicPlayHistory[id]!!.totalPlaytime)
    }

/*
    fun getPlaytime(id:Int) :Long {
        if (!musicPlayHistory.containsKey(id)) return 0

        return musicPlayHistory[id]!!.totalPlaytime
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
    */
}