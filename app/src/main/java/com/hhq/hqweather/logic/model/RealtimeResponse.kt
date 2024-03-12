package com.hhq.hqweather.logic.model

import com.google.gson.annotations.SerializedName

data class RealtimeResponse(val status:String, val result: Result) {
    data class AQI(val chn:Float)
    data class AirQuality(val aqi:AQI)
    data class Realtime(val skycon:String,val temperature: Float, @SerializedName("air_quality") val  airQuality: AirQuality)

    data class Result(val realtime: Realtime)

}