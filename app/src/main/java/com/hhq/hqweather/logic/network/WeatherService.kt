package com.hhq.hqweather.logic.network

import com.hhq.hqweather.HqApplication
import com.hhq.hqweather.logic.model.DailyResponse
import com.hhq.hqweather.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherService {

    @GET("v2.5/${HqApplication.WEATHER_TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng") lng:String, @Path("lat") lat:String):Call<RealtimeResponse>

    @GET("v2.5/${HqApplication.WEATHER_TOKEN}/{lng},{lat}/daily.json")
    fun getDailyWeather(@Path("lng") lng:String, @Path("lat") lat:String):Call<DailyResponse>
}