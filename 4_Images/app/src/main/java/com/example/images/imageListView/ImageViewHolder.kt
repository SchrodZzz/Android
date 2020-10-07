package com.example.images.imageListView

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.images.R

class ImageViewHolder(val root: View) : RecyclerView.ViewHolder(root) {
    val imageView: ImageView = root.findViewById(R.id.item_image)
    val descriptionTextView: TextView = root.findViewById(R.id.item_description)
}