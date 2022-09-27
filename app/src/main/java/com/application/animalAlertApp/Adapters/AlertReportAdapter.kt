package com.application.animalAlertApp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.application.animalAlertApp.Interfaces.SelectReportOption
import com.application.animalAlertApp.R


class AlertReportAdapter(val context: Context,private val inter: SelectReportOption):RecyclerView.Adapter<AlertReportAdapter.ViewHolder>() {
    val list = arrayListOf<String>(
        "Violence",
        "Inappropriate",
        "Selling illegal",
        "Harrasment",
        "Spam"
    )

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val checkbox=itemView.findViewById<CheckBox>(R.id.checkbox)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.report_alert_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.checkbox.setText("  "+list[position])
        holder.checkbox.setOnCheckedChangeListener(object :CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if (p1){
                    inter.onOptionSelect(position,list[position])
                }else {
                    inter.onOptionDeSelect(position,list[position])
                }
            }

        })
    }

    override fun getItemCount(): Int {
      return list.size
    }
}