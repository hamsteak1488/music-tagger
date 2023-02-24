package com.cookandroid.myapplication

class SituationHistory {

    private var historyPointMap = HashMap<String, Int>()


    fun AddMusic(name:String) : Boolean {
        if (!historyPointMap.containsKey(name)) {
            historyPointMap[name] = 0
            return true
        }
        else return false
    }

    fun AddPoint(name:String, point:Int) : Boolean {
        if (historyPointMap.containsKey(name)) {
            historyPointMap[name]!!.plus(point)
            return true
        }
        else return false
    }
}