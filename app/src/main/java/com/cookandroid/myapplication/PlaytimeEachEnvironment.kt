package com.cookandroid.myapplication

import org.json.JSONObject

class PlaytimeEachEnvironment {

    var totalPlaytime:Long = 0

    //private var timeOfDay = HashMap<CharSequence, Long>()
    //private var weather = HashMap<CharSequence, Long>()
    //private var season = HashMap<CharSequence, Long>()

    private var tagInfo = HashMap<CharSequence, HashMap<CharSequence, Long>>()
    
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
    }

    fun set(jo:JSONObject) {

        val categoryKeys = jo.keys()
        while (categoryKeys.hasNext()) {
            val categoryKey = categoryKeys.next()
            val tagJO = jo.get(categoryKey) as JSONObject
            val tagKeys = tagJO.keys()
            while (tagKeys.hasNext()) {
                val tagKey = tagKeys.next()
                tagInfo[categoryKey]!![tagKey] = tagJO.get(tagKey).toString().toLong()
            }
        }
    }

    fun toJson() : JSONObject {
        val tagInfoJO = JSONObject()

        for (category in tagInfo) {
            val categoryJO = JSONObject()

            for (tag in category.value) {
                categoryJO.put(tag.key.toString(), tag.value)
            }

            tagInfoJO.put(category.key.toString(), categoryJO)
        }
        return tagInfoJO
    }
}