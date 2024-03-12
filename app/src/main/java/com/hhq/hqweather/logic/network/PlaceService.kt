package com.hhq.hqweather.logic.network

import com.hhq.hqweather.HqApplication
import com.hhq.hqweather.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceService {
    @GET("v2/place?token=${HqApplication.WEATHER_TOKEN}&lang=zh_CN")
    fun searchPlaces(@Query("query") query: String):Call<PlaceResponse>
}