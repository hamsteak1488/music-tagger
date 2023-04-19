package com.cookandroid.myapplication

import android.util.Log
import com.google.gson.JsonObject
import kotlin.collections.HashMap

class PlayHistory {

    var totalPlaytime:Long = 0

    private var tagInfoMap = HashMap<CharSequence, HashMap<CharSequence, Long>>()

    init {
        val surroundings = Surroundings()

        for (category in surroundings.info.keys) {
            tagInfoMap.put(category, HashMap())
        }
    }

    fun addPlaytime(playtime:Long) {
        totalPlaytime += playtime

        SurroundingsManager.getCurrentSurroundings { surroundings ->
            for (category in tagInfoMap) {
                val infoNow = surroundings.info[category.key]!!
                if (!category.value.containsKey(infoNow)) {
                    category.value.put(infoNow, 0)
                }
                category.value[infoNow] = category.value[infoNow]!!.plus(playtime)
            }
        }
    }

    fun set(jo:JsonObject) {

        val categoryKeys = jo.keySet().iterator()
        while (categoryKeys.hasNext()) {
            val categoryKey = categoryKeys.next()
            val tagJO = jo.get(categoryKey) as JsonObject
            val tagKeys = tagJO.keySet().iterator()
            while (tagKeys.hasNext()) {
                val tagKey = tagKeys.next()
                tagInfoMap[categoryKey]!![tagKey] = tagJO.get(tagKey).toString().toLong()
            }
        }
    }

    fun toJson() : JsonObject {
        val tagInfoJO = JsonObject()

        for (category in tagInfoMap) {
            val categoryJO = JsonObject()

            for (tag in category.value) {
                categoryJO.addProperty(tag.key.toString(), tag.value)
            }

            tagInfoJO.add(category.key.toString(), categoryJO)
        }
        return tagInfoJO
    }
}