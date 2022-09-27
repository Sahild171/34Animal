package com.application.animalAlertApp.Utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.application.animalAlertApp.R

class CustomSpinnerAdapter(
    context: Context,
    val list: ArrayList<String>,
    val prioritylistcolor: ArrayList<Int>
) : ArrayAdapter<String>(context, 0, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent);
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent);
    }

    fun createItemView(position: Int, recycledView: View?, parent: ViewGroup): View {
        val country = getItem(position)

        val view = recycledView ?: LayoutInflater.from(context).inflate(
            R.layout.item_custom_spinner,
            parent,
            false
        )

        val logo = view.findViewById<View>(R.id.logo) as ImageView
        val title = view.findViewById<View>(R.id.title) as TextView

        title.setText(list[position])
         logo.setImageResource(prioritylistcolor[position])




        return view
    }


}
