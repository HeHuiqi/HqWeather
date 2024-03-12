package com.hhq.hqweather.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.hhq.hqweather.HqApplication
import com.hhq.hqweather.logic.model.Place

object PlaceDao {
    fun savePlace(place: Place) {
        sharedPreferences().edit {
            putString("place", Gson().toJson(place))
        }
    }
    fun getSavedPlace(): Place {
        val placeJson = sharedPreferences().getString("place", "")
        return Gson().fromJson(placeJson, Place::class.java)
    }
    fun isPlaceSaved() = sharedPreferences().contains("place")
    private fun sharedPreferences() = HqApplication.context.
    getSharedPreferences("hq_weather", Context.MODE_PRIVATE)
}