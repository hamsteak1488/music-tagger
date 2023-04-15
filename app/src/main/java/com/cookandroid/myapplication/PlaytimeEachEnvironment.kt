package com.cookandroid.myapplication

import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.cookandroid.myapplication.activities.MainActivity
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class PlaytimeEachEnvironment {

    private val now = System.currentTimeMillis()
    private val date = Date(now)
    lateinit var nowWeather: String
    var totalPlaytime:Long = 0

    private var tagInfoMap = HashMap<CharSequence, HashMap<CharSequence, Long>>()

    init {
        tagInfoMap.put("시간", HashMap())
        tagInfoMap.put("날씨", HashMap())
        tagInfoMap.put("계절", HashMap())
        tagInfoMap.put("요일", HashMap())
    }
    companion object{
        var requestQueue: RequestQueue? = null
    }
    
    // 현재가 하루 중 어느 때인지 반환
    private fun getCurrentInformation(categoryName:CharSequence) : CharSequence {

        // TODO: categoryName에 따라 시간, 날씨, 계절 정보 등을 문자열로 반환시켜줘야함
        return when(categoryName){
            "시간" -> getTimeOfDay()
            "날씨" -> getWeather()
            "계절" -> getSeason()
            "요일" -> getWeekDay()
            else -> "false"
        }
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


    // 현재가 하루 중 어느 때인지 반환
    private fun getTimeOfDay() : CharSequence {
        val simpleDateFormatHour = SimpleDateFormat("HH")
        val currentHour = simpleDateFormatHour.format(date)
        val realHour = currentHour.toInt()+9 //한국 시각과 9시간 차이남
        if(realHour >=24){realHour - 24}
        return when (realHour) {
            in 0..6 -> "dawn"
            in 7..9 -> "earlyMorning"
            in 10..12 -> "lateMorning"
            in 13..15 -> "earlyAfternoon"
            in 16..18 -> "lateAfternoon"
            in 19..21 -> "evening"
            else -> "night"
        }
    }
    private fun getWeather(): CharSequence {
        //return currentWeatherCall()
        var weather = -1
        return when(weather){
            1 -> "rain"
            2 -> "snow"
            else -> "etc"
        }
    }

    private fun currentWeatherCall(operation:(currentWeather:String)->Unit){
        if (requestQueue == null) requestQueue = Volley.newRequestQueue(MainActivity.applicationContext())
        val url = "https://api.openweathermap.org/data/2.5/weather?q=Incheon&appid=ef84cf6df878d02ffc040001775a3d1f"
        //I18 국제화
        val request: StringRequest =
            object : StringRequest(Method.GET, url, Response.Listener { response: String ->
                try {
                    val jsonObject = JSONObject(response)
                    val weatherJson = jsonObject.getJSONArray("weather")
                    val weatherObj = weatherJson.getJSONObject(0)
                    val weather = weatherObj.getString("description")

                    operation(weather)

                } catch (e: JSONException) { e.printStackTrace() }
            }, Response.ErrorListener { }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    return java.util.HashMap() }
            }
        request.setShouldCache(false)
        requestQueue!!.add(request)
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
    private fun getWeekDay(): CharSequence {
        val simpleDateFormatDayOfWeek = SimpleDateFormat("EE", Locale.KOREAN)
        return simpleDateFormatDayOfWeek.format(date)
    }
}