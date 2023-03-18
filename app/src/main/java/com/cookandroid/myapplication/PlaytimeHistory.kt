package com.cookandroid.myapplication

import android.util.Log
import org.json.JSONObject

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

    fun set(jo: JSONObject) {
        val keys = jo.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            playtimeHistoryMap[key.toInt()]?.set(jo.get(key) as JSONObject)
        }
    }

    fun toJson() : JSONObject {
        val playtimeHistoryJO = JSONObject()

        for (tagInfo in playtimeHistoryMap) {
            playtimeHistoryJO.put(tagInfo.key.toString(), tagInfo.value.toJson())
        }

        return playtimeHistoryJO
    }
}