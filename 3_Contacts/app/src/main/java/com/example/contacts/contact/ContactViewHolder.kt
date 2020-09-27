package com.example.contacts.contact

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*

class ContactViewHolder(val root: View) : RecyclerView.ViewHolder(root) {
    val contactNameText: TextView = root.contact_name
    val contactPhoneText: TextView = root.contact_phone
}