package com.cookandroid.myapplication

class PlaytimeEachEnvironment {

    var totalPlayTime:Long = 0

    private var timeOfDay = HashMap<CharSequence, Long>()
    private var weather = HashMap<CharSequence, Long>()
    private var temperature = HashMap<CharSequence, Long>()


    private fun getTimeOfDay() : CharSequence {
        return "morning"
    }
    private fun getWeather() : CharSequence {
        return "sunny"
    }
    private fun getTemperature() : CharSequence {
        return "warm";
    }

    fun addPlaytime(playtime:Long) {

        totalPlayTime += playtime

        val timeNow = getTimeOfDay()
        val weatherNow = getWeather()
        val temperatureNow = getTemperature()

        timeOfDay[timeNow] = timeOfDay.getOrDefault(timeNow, 0) + playtime
        weather[weatherNow] = weather.getOrDefault(weatherNow, 0) + playtime
        temperature[temperatureNow] = temperature.getOrDefault(temperatureNow, 0) + playtime
    }
}