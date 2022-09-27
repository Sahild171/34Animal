package com.application.animalAlertApp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.animalAlertApp.Adapters.TitleAdapter
import com.application.animalAlertApp.R

/**
 * A simple [Fragment] subclass.
 * Use the [TitleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TitleFragment : Fragment() {
private lateinit var recyclerview:RecyclerView
lateinit var adapter:TitleAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view:View=inflater.inflate(R.layout.fragment_title, container, false)
        init(view)

        return view;
    }

    private fun init(view: View) {
        recyclerview=view.findViewById(R.id.recyclerview)
        adapter=TitleAdapter(context)
        recyclerview.adapter=adapter
    }


}