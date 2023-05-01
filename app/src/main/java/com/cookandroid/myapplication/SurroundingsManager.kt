package com.cookandroid.myapplication

import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.cookandroid.myapplication.activities.MainActivity
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

import com.tftf.util.Surroundings;

object SurroundingsManager {

    var weatherUpdateTime:Long = 0
    var currentWeather:String = ""

    var requestQueue: RequestQueue? = null

    fun getCurrentSurroundings(callbackOperation: (Surroundings) -> Unit) {

        val surroundings = Surroundings()

        surroundings.infoMap["시간"] = getTimeOfDay()
        surroundings.infoMap["요일"] = getWeekDay()
        surroundings.infoMap["계절"] = getSeason()

        if (System.currentTimeMillis() - weatherUpdateTime > 1000 * 60 * 60) {
            updateWeather {
                surroundings.infoMap["날씨"] = currentWeather
                callbackOperation(surroundings)
            }
        }
        else {
            surroundings.infoMap["날씨"] = currentWeather
            callbackOperation(surroundings)
        }
    }

    // 현재가 하루 중 어느 때인지 반환
    fun getTimeOfDay() : String {
        return when (LocalTime.now(ZoneId.of("Asia/Seoul")).hour) {
            in 1..5 -> "새벽"
            in 6..8 -> "이른 아침"
            in 9..11 -> "늦은 아침"
            in 12..15 -> "이른 오후"
            in 16..18 -> "늦은 오후"
            in 19..23 -> "저녁"
            else -> "밤"
        }
    }

    fun updateWeather(callbackOperation:()->Unit) {
        currentWeatherCall {
            weatherUpdateTime = System.currentTimeMillis()
            // https://openweathermap.org/weather-conditions
            currentWeather = when (it) {
                "clear sky" -> "맑음"
                "few clouds" -> "구름"
                "scattered clouds" -> "구름 흩뿌려짐"
                "broken clouds" -> "구름 많이 낌"
                "shower rain" -> "소나기"
                "rain" -> "비"
                "thunderstorm" -> "천둥"
                "snow" -> "눈"
                "mist" -> "안개"

                else -> "예외 날씨"
            }
            callbackOperation()
        }
        return
    }

    private fun currentWeatherCall(operation:(String)->Unit){
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


    fun getSeason() : String {
        return when (LocalDate.now(ZoneId.of("Asia/Seoul")).monthValue) {
            in 3..5 -> "봄"
            in 6..8 -> "여름"
            in 9..11 -> "가을"
            else -> "겨울"
        }
    }


    fun getWeekDay(): String {
        return when (LocalDate.now(ZoneId.of("Asia/Seoul")).dayOfWeek.value) {
            1 -> "월"
            2 -> "화"
            3 -> "수"
            4 -> "목"
            5 -> "금"
            6 -> "토"
            7 -> "일"
            else -> "요일 오류"
        }
    }
}