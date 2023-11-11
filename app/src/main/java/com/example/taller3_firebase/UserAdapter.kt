package com.example.taller3_firebase

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class UserAdapter(context: Context, users: List<User>) : ArrayAdapter<User>(context, 0, users) {

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false)
        }

        val user = getItem(position)
        val text1View = itemView!!.findViewById<TextView>(android.R.id.text1)
        val text2View = itemView.findViewById<TextView>(android.R.id.text2)

        // Set layout gravity to center
        text1View.gravity = Gravity.CENTER
        text2View.gravity = Gravity.CENTER

        text1View.text = user?.email
        text2View.text = "${user?.firstName} ${user?.lastName}"

        return itemView
    }
}