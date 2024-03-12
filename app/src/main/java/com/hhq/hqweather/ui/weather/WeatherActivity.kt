package com.hhq.hqweather.ui.weather

import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hhq.hqweather.R
import com.hhq.hqweather.databinding.ActivityWeatherBinding
import com.hhq.hqweather.databinding.ForecastBinding
import com.hhq.hqweather.databinding.LifeIndexBinding
import com.hhq.hqweather.databinding.NowBinding
import com.hhq.hqweather.logic.model.Weather
import com.hhq.hqweather.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy {
        ViewModelProvider(this).get(WeatherViewModel::class.java)
    }
    private val rootBinding:ActivityWeatherBinding by lazy {
        ActivityWeatherBinding.inflate(layoutInflater)
    }
    private val nowBinding by lazy {
        //include 子 layout 为其指定一个id，并调用bind()方法来初始化
        NowBinding.bind(rootBinding.nowLayout.root)
    }
    private val lifeIndexBinding by  lazy {
        //include 子 layout 为其指定一个id，并调用bind()方法来初始化
        LifeIndexBinding.bind(rootBinding.lifeIndexLayout.root)
    }
    private val forecastBinding by lazy {
        //include 子 layout 为其指定一个id，并调用bind()方法来初始化
        ForecastBinding.bind(rootBinding.forecastLayout.root)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //必须在设置view之前调用
        setupStateBar()
        setContentView(rootBinding.root)
        setup()
    }

    private fun setupStateBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            val decorView = window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
        window.statusBarColor = Color.TRANSPARENT
    }
    private fun setupDrawer(){
        nowBinding.navBtn.setOnClickListener {
            rootBinding.drawerLayout.openDrawer(GravityCompat.START)
        }
        rootBinding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
            } })
    }
    private fun setupViewModel(){
        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?:""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?:""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?:""
        }
        viewModel.weatherLiveData.observe(this, Observer { result ->
            val  weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            rootBinding.swipeRefresh.isRefreshing = false
        })
    }
    private fun setup() {

        setupDrawer()

        setupViewModel()

        refreshWeather()

        setupSwipeRefresh()
    }
    private fun setupSwipeRefresh(){
        rootBinding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        rootBinding.swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }
    }
    private fun refreshWeather(){
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        rootBinding.swipeRefresh.isRefreshing = true
    }
    private fun showWeatherInfo(weather: Weather) {

        nowBinding.placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        // 填充now.xml布局中的数据
        val currentTempText = "${realtime.temperature.toInt()} °C"
        nowBinding.currentTemp.text = currentTempText
        nowBinding.currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        nowBinding.currentAQI.text = currentPM25Text
        nowBinding.root.setBackgroundResource(getSky(realtime.skycon).bg)

        // 填充forecast.xml布局中的数据
        forecastBinding.forecastLayoutContent.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                forecastBinding.root, false)
            val dateInfo:TextView = view.findViewById(R.id.dateInfo)
            val skyIcon: ImageView = view.findViewById(R.id.skyIcon)
            val skyInfo:TextView = view.findViewById(R.id.skyInfo)
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} °C"
            temperatureInfo.text = tempText
            forecastBinding.forecastLayoutContent.addView(view)
        }

        // 填充life_index.xml布局中的数据
        val lifeIndex = daily.lifeIndex
        lifeIndexBinding.coldRiskText.text = lifeIndex.coldRisk[0].desc
        lifeIndexBinding.dressingText.text = lifeIndex.dressing[0].desc
        lifeIndexBinding.ultravioletText.text = lifeIndex.ultraviolet[0].desc
        lifeIndexBinding.carWashingText.text = lifeIndex.carWashing[0].desc
        rootBinding.weatherScrollView.visibility = View.VISIBLE

    }
}