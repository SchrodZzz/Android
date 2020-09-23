package com.example.weather.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.weather.R
import kotlinx.android.synthetic.main.fragment_today.*

class TodayFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_today, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        today_city.text = getString(R.string.mock_city)
        today_weatherImg.setImageResource(R.drawable.wi_day_sunny_overcast)
        today_temp.text = getString(R.string.template_tempC, -42)

        setupExtra(
            today_wind,
            getString(R.string.windDescription),
            R.drawable.wi_windy,
            getString(R.string.template_windPower, 4.2)
        )
        setupExtra(
            today_pressure,
            getString(R.string.pressureDescription),
            R.drawable.wi_barometer,
            getString(R.string.template_pressure, 742)
        )
        setupExtra(
            today_humidity,
            getString(R.string.humidityDescription),
            R.drawable.wi_humidity,
            getString(R.string.template_percents, 42)
        )
    }

    private fun setupExtra(extraView: View, description: String, imageId: Int, value: String) {
        extraView.findViewById<TextView>(R.id.extra_header).text = description
        extraView.findViewById<ImageView>(R.id.extra_image).setImageResource(imageId)
        extraView.findViewById<TextView>(R.id.extra_value).text = value
    }
}