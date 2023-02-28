package com.cookandroid.myapplication

class PlaytimeEachEnvironment {

    var totalPlaytime:Long = 0

    private var timeOfDay = HashMap<CharSequence, Long>()
    private var weather = HashMap<CharSequence, Long>()
    private var season = HashMap<CharSequence, Long>()
    
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

        timeOfDay[timeNow] = timeOfDay.getOrDefault(timeNow, 0) + playtime
        weather[weatherNow] = weather.getOrDefault(weatherNow, 0) + playtime
        season[seasonNow] = season.getOrDefault(seasonNow, 0) + playtime
    }
}