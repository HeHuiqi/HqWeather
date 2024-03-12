package com.hhq.hqweather.ui.place

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hhq.hqweather.MainActivity
import com.hhq.hqweather.R
import com.hhq.hqweather.databinding.FragmentPlaceBinding
import com.hhq.hqweather.ui.weather.WeatherActivity

class PlaceFragment:Fragment() {
    val viewModel by lazy {
        ViewModelProvider(this).get(PlaceViewModel::class.java)
    }
    private lateinit var adapter: PlaceAdapter
    private val rootBinding by lazy {
        FragmentPlaceBinding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        return inflater.inflate(R.layout.fragment_place,container,false)
        return rootBinding.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (activity is MainActivity && viewModel.isPlaceSaved()) {

            val place = viewModel.getSavedPlace()
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }

        val layoutManager = LinearLayoutManager(activity)
        rootBinding.recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this,viewModel.placeList)
        rootBinding.recyclerView.adapter = adapter
        //设置输入框编辑事件
        rootBinding.searchPlaceEdit.addTextChangedListener { editable ->
            val content = editable.toString()
            if (content.isNotEmpty()) {
                //根据输入开始请求数据
                viewModel.searchPlaces(content)
            } else {
                rootBinding.recyclerView.visibility = View.GONE
                rootBinding.bgImageView.visibility = View.VISIBLE
            }
        }

        //监听LiveData数据变化
        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer { result ->
            //有结果返回开始刷新UI或执行其他逻辑
            val places = result.getOrNull()
            if (places != null) {
                rootBinding.recyclerView.visibility = View.VISIBLE
                rootBinding.bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            } else {
                val toast = Toast.makeText(activity,"查询不到地址",Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER,0,0)
                toast.show()
                result.exceptionOrNull()?.printStackTrace()
            }

        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}