package com.application.animalAlertApp.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ExpandableListAdapter
import com.application.animalAlertApp.Adapters.CustomExpandableListAdapter
import com.application.animalAlertApp.R
import com.application.animalAlertApp.databinding.ActivityFaqBinding

class FaqActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFaqBinding
    lateinit var adapter: ExpandableListAdapter
    lateinit var titleList: ArrayList<String>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFaqBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setExpandableListView()
    }

    private fun setExpandableListView() {
        if (binding.expandableListView != null) {
            val listData = data
            titleList = ArrayList()



            titleList.add("What is the Animal Alert Community?")
            titleList.add("Is my personal information protected?")
            titleList.add("How does this app work?")
            titleList.add("What is the alert system?")
            titleList.add("What is the subscription for?")
            titleList.add("Can I purchase animals on the app?")


            adapter = CustomExpandableListAdapter(this, titleList , listData)
            binding.expandableListView.setAdapter(adapter)

            binding.expandableListView.setOnGroupExpandListener { groupPosition ->
//                Toast.makeText(
//                    applicationContext,
//                    (titleList as ArrayList<String>)[groupPosition] + " List Expanded.",
//                    Toast.LENGTH_SHORT
//                ).show()
            }

            binding.expandableListView.setOnGroupCollapseListener { groupPosition ->
//                Toast.makeText(
//                    applicationContext,
//                    (titleList as ArrayList<String>)[groupPosition] + " List Collapsed.",
//                    Toast.LENGTH_SHORT
//                ).show()
            }

            binding.expandableListView.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
//                Toast.makeText(
//                    applicationContext,
//                    "Clicked: " + (titleList as ArrayList<String>)[groupPosition] + " -> " + listData[(titleList as ArrayList<String>)[groupPosition]]!!.get(
//                        childPosition
//                    ),
//                    Toast.LENGTH_SHORT
//                ).show()
                false
            }
        }
    }

    fun CloseFaq(view: android.view.View) {
        finish()
    }

    val data: HashMap<String, List<String>>
        get() {
            val listData = HashMap<String, List<String>>()

            val child1 = ArrayList<String>()
            child1.add(resources.getString(R.string.What_is_AAC))

            val child2 = ArrayList<String>()
            child2.add(resources.getString(R.string.is_my_personal_info))

            val child3 = ArrayList<String>()
            child3.add(resources.getString(R.string.how_does_work))


            val child4 = ArrayList<String>()
            child4.add(resources.getString(R.string.alert_system))


            val child5 = ArrayList<String>()
            child5.add(resources.getString(R.string.subscription_for))

            val child6 = ArrayList<String>()
            child6.add(resources.getString(R.string.purchase_animal))


            listData.put("What is the Animal Alert Community?",child1)
            listData.put("Is my personal information protected?",child2)
            listData.put("How does this app work?",child3)
            listData.put("What is the alert system?",child4)
            listData.put("What is the subscription for?",child5)
            listData.put("Can I purchase animals on the app?",child6)

          //  listData["What is the Animal Alert Community?"] = child1
//            listData["Is my personal information protected?"] = child2
//            listData["How does this app work?"] = child3
//            listData["What is the alert system?"] = child4
//            listData["What is the subscription for?"] = child5
//            listData["Can I purchase animals on the app?"] = child6

            return listData
        }
}