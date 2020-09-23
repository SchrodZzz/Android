package com.example.weather.items

import android.annotation.SuppressLint
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import kotlinx.android.synthetic.main.week_day.view.*
import java.util.*

data class Day(
    val date: Date
)

class DayAdapter(
    private val context: Context,
    private val days: List<Day>
) : RecyclerView.Adapter<DayViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder =
        DayViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.week_day,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = days.size

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.descriptionText.text = DESCRIPTION_FORMATTER.format(days[position].date)
        holder.image.setImageResource(
            listOf(
                R.drawable.wi_night_rain_wind,
                R.drawable.wi_snow_wind,
                R.drawable.wi_sandstorm,
                R.drawable.wi_tornado,
                R.drawable.wi_day_sprinkle
            )[(Math.random() * 5).toInt()]
        )
        holder.tempText.text = context.getString(R.string.template_tempC, (Math.random() * 20).toInt() + 5)
    }


    companion object {
        @SuppressLint("SimpleDateFormat")
        private val DESCRIPTION_FORMATTER = SimpleDateFormat("EEE, dd MMM")
    }
}

class DayViewHolder(val root: View) : RecyclerView.ViewHolder(root) {
    val descriptionText: TextView = root.day_description
    val image: ImageView = root.day_image
    val tempText: TextView = root.day_temp
}