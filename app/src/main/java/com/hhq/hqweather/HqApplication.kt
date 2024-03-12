package com.hhq.hqweather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class HqApplication:Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context
        const val WEATHER_TOKEN = "JPLK7gQJmQ1e7p0Y"
    }
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}