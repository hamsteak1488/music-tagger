package com.cookandroid.myapplication

import com.google.gson.JsonObject

class PlaytimeEachEnvironment {


    var totalPlaytime:Long = 0

    private var tagInfoMap = HashMap<CharSequence, HashMap<CharSequence, Long>>()

    init {
        tagInfoMap.put("시간", HashMap())
        tagInfoMap.put("날씨", HashMap())
        tagInfoMap.put("계절", HashMap())
    }
    
    // 현재가 하루 중 어느 때인지 반환
    private fun getCurrentInformation(categoryName:CharSequence) : CharSequence {

        // TODO: categoryName에 따라 시간, 날씨, 계절 정보 등을 문자열로 반환시켜줘야함

        return "asdf"
    }

    fun addPlaytime(playtime:Long) {

        totalPlaytime += playtime

        for (category in tagInfoMap) {
            val infoNow = getCurrentInformation(category.key)
            if (!category.value.containsKey(infoNow)) {
                category.value.put(infoNow, 0)
            }
            category.value[infoNow] = category.value[infoNow]!!.plus(playtime)
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