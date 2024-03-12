package com.hhq.hqweather.logic

import androidx.lifecycle.liveData
import com.hhq.hqweather.logic.dao.PlaceDao
import com.hhq.hqweather.logic.model.Place
import com.hhq.hqweather.logic.model.Weather
import com.hhq.hqweather.logic.network.HqWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

object Repository {

    // 一般在仓库层中定义的方法，为了能将异步获取的数据以响应式编程的方式通知给上一层，通 常会返回一个LiveData对象

    // liveData()函数是lifecycle-livedata-ktx库提供的 一个非常强大且好用的功能，
    // 它可以自动构建并返回一个LiveData对象，然后在它的代码块中 提供一个挂起函数的上下文，
    // 这样我们就可以在liveData()函数的代码块中调用任意的挂起 函数了

    // liveData()函数的线程参数类型指定成了 Dispatchers.IO，
    // 这样代码块中的所有代码就都运行在子线程中了
    fun searchPlaces2(query: String) = liveData(Dispatchers.IO) {
        val result = try {
            val placeResponse = HqWeatherNetwork.searchPlaces(query)
            if (placeResponse.status == "ok") {
                val places = placeResponse.places
                Result.success(places)
            } else {
                Result.failure(RuntimeException("response status is${placeResponse.status}"))
            }
        } catch (e: Exception) {
            Result.failure<List<Place>>(e)
        }
        // emit()方法其实类似于调用LiveData的 setValue()方法来通知数据变化
        emit(result)
    }

    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = HqWeatherNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is${placeResponse.status}"))
        }
    }

    fun refreshWeather(lng:String,lat:String) = fire(Dispatchers.IO) {
            coroutineScope {
                // 由于 async函数必须在协程作用域内才能调用，所以这里又使用coroutineScope函数创建了一个 协程作用域
                val deferredRealtime = async {
                    HqWeatherNetwork.getRealtimeWeather(lng,lat)
                }
                val deferredDaily = async {
                    HqWeatherNetwork.getDailyWeather(lng,lat)
                }
                val realtimeResponse = deferredRealtime.await()
                val  dailyResponse = deferredDaily.await()
                if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                    val weather = Weather(realtimeResponse.result.realtime,dailyResponse.result.daily)
                    Result.success(weather)
                } else {
                    Result.failure(RuntimeException("realtime response status is ${realtimeResponse.status}" +
                            "daily response status is ${dailyResponse.status}"))
                }

            }
    }

    //统一对异常进行处理
    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }
    fun savePlace(place: Place) = PlaceDao.savePlace(place)
    fun getSavedPlace() = PlaceDao.getSavedPlace()
    fun isPlaceSaved() = PlaceDao.isPlaceSaved()
}