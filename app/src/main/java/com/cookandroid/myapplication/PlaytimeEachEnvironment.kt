package com.cookandroid.myapplication

import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.util.*
import kotlin.collections.HashMap

class PlaytimeEachEnvironment {

    var totalPlaytime:Long = 0

    private val now = System.currentTimeMillis()
    private val date = Date(now)

    private var timeOfDay = HashMap<CharSequence, Long>()
    private var weather = HashMap<CharSequence, Long>()
    private var season = HashMap<CharSequence, Long>()
    private var weekDay = HashMap<CharSequence, Long>()
    
    // 현재가 하루 중 어느 때인지 반환
    private fun getTimeOfDay() : CharSequence {
        val simpleDateFormatHour = SimpleDateFormat("HH")
        val currentHour = simpleDateFormatHour.format(date)
        return when (currentHour.toInt()) { //단순 월로 3개월 단위 계절 구분
            in 0..6 -> "dawn"
            in 6..9 -> "earlyMorning"
            in 9..12 -> "lateMorning"
            in 12..3 -> "earlyAfternoon"
            in 3..6 -> "lateAfternoon"
            in 6..9 -> "evening"
            else -> "night"
        }
    }
    private fun getWeather() : CharSequence {
        return "sunny"
    }
    private fun getSeason() : CharSequence {
        val simpleDateFormatMonth = SimpleDateFormat("MM")
        val currentMonth = simpleDateFormatMonth.format(date)
        return when (currentMonth.toInt()) { //단순 월로 3개월 단위 계절 구분
            in 3..5 -> "spring"
            in 6..8 -> "summer"
            in 9..11 -> "autumn"
            else -> "winter"
        }
    }
    private fun getWeekDay(): CharSequence{
        val simpleDateFormatDayOfWeek = SimpleDateFormat("EE")
        val currentWeekDay = simpleDateFormatDayOfWeek.format(date)
        return when (currentWeekDay.toInt()) {
            1 -> return "sun"
            2 -> return "mon"
            3 -> return "tue"
            4 -> return "wed"
            5 -> return "thu"
            6 -> return "fri"
            else -> return "sun"
        }
    }

    fun addPlaytime(playtime:Long) {

        totalPlaytime += playtime

        val timeNow = getTimeOfDay()
        val weatherNow = getWeather()
        val seasonNow = getSeason()
        val dayNow = getWeekDay()

        timeOfDay[timeNow] = timeOfDay.getOrDefault(timeNow, 0) + playtime
        weather[weatherNow] = weather.getOrDefault(weatherNow, 0) + playtime
        season[seasonNow] = season.getOrDefault(seasonNow, 0) + playtime
        weekDay[dayNow] = weekDay.getOrDefault(dayNow,0) + playtime
    }
}