package com.cookandroid.myapplication

import com.google.gson.JsonObject
import org.json.JSONObject

class PlaytimeEachEnvironment {


    var totalPlaytime:Long = 0

    //private var timeOfDay = HashMap<CharSequence, Long>()
    //private var weather = HashMap<CharSequence, Long>()
    //private var season = HashMap<CharSequence, Long>()

    private var tagInfo = HashMap<CharSequence, HashMap<CharSequence, Long>>()

    init {
        if (!tagInfo.containsKey("날씨")) tagInfo.put("날씨", HashMap())
    }
    
    // 현재가 하루 중 어느 때인지 반환
    private fun getTimeOfDay() : CharSequence {
        return "morning"
    }
    private fun getWeather() : CharSequence {
        return "sunny"
    }
    private fun getSeason() : CharSequence {
        return "warm";
    }

    fun addPlaytime(playtime:Long) {

        totalPlaytime += playtime

        val timeNow = getTimeOfDay()
        val weatherNow = getWeather()
        val seasonNow = getSeason()

        //timeOfDay[timeNow] = timeOfDay.getOrDefault(timeNow, 0) + playtime
        //weather[weatherNow] = weather.getOrDefault(weatherNow, 0) + playtime
        //season[seasonNow] = season.getOrDefault(seasonNow, 0) + playtime

        tagInfo["날씨"]!!["비"] = tagInfo["날씨"]!!.getOrDefault("비", 0) + playtime
    }

    fun set(jo:JsonObject) {

        val categoryKeys = jo.keySet().iterator()
        while (categoryKeys.hasNext()) {
            val categoryKey = categoryKeys.next()
            val tagJO = jo.get(categoryKey) as JsonObject
            val tagKeys = tagJO.keySet().iterator()
            while (tagKeys.hasNext()) {
                val tagKey = tagKeys.next()
                tagInfo[categoryKey]!![tagKey] = tagJO.get(tagKey).toString().toLong()
            }
        }
    }

    fun toJson() : JsonObject {
        val tagInfoJO = JsonObject()

        for (category in tagInfo) {
            val categoryJO = JsonObject()

            for (tag in category.value) {
                categoryJO.addProperty(tag.key.toString(), tag.value)
            }

            tagInfoJO.add(category.key.toString(), categoryJO)
        }
        return tagInfoJO
    }
}