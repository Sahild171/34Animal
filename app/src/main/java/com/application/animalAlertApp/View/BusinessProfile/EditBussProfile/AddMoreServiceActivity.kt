package com.application.animalAlertApp.View.BusinessProfile.EditBussProfile

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.animalAlertApp.Adapters.SelectServiceAdapter
import com.application.animalAlertApp.Interfaces.ShopServiceInterface
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.R
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.View.Shops.ShopViewModel
import com.application.animalAlertApp.databinding.ActivityAddMoreServiceBinding
import com.jaredrummler.materialspinner.MaterialSpinner
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONObject

@AndroidEntryPoint
class AddMoreServiceActivity : AppCompatActivity(), ShopServiceInterface {
    private lateinit var binding: ActivityAddMoreServiceBinding
    private var service: String = ""
    private var pricetype: String = ""
    private val viewmodel: ShopViewModel by viewModels()
    private val mainjsonobject = JSONObject()
    private val jsonArray = JSONArray()
    private lateinit var adapter: SelectServiceAdapter
    private lateinit var servicelist: ArrayList<String>
    private var prioritylist = arrayListOf<String>("Hourly", "Weekly", "Monthly","One time")
    private lateinit var pd:Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMoreServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pd= MyProgressBar.progress(this)
        servicelist = ArrayList()

        binding.recyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        getservices()

        binding.btContinue.setOnClickListener {
            if (jsonArray.length() > 0) {
                Addserivcestoshop()
            } else {
                if (validations()) {
                    val jsonservice = JSONObject()
                    jsonservice.put("Service", service)
                    jsonservice.put("price", binding.etPrice.text.toString().trim())
                    jsonservice.put("pricePeriod", pricetype)
                    jsonservice.put("serviceDescription", binding.etBusdescription.text.toString().trim())
                    jsonArray.put(jsonservice)
                    mainjsonobject.put("services", jsonArray)
                    Addserivcestoshop()
                }
            }
        }

        binding.imgAddmore.setOnClickListener {
            if (validations()) {
                addmorefunctionality()
            }
        }

        binding.imgBack.setOnClickListener {
            finish()
        }
    }

    fun addmorefunctionality() {
        val jsonservice = JSONObject()
        jsonservice.put("Service", service)
        jsonservice.put("price", binding.etPrice.text.toString().trim())
        jsonservice.put("pricePeriod", pricetype)
        jsonservice.put("serviceDescription", binding.etBusdescription.text.toString().trim())
        jsonArray.put(jsonservice)
        mainjsonobject.put("services", jsonArray)

        adapter = SelectServiceAdapter(this, jsonArray, this)
        binding.recyclerview.adapter = adapter

        binding.etBusdescription.setText("")
        binding.etPrice.setText("")
    }


    private fun Addserivcestoshop() {
        viewmodel.addshopservices(jsonArray.toString())
        viewmodel.addserivcedata.observe(this, Observer {
            when (it) {
                is Resource.Success -> {

                    if (it.value.status == 200) {
                        toast("Service Added")
                        pd.dismiss()
                        val intent = Intent()
                        intent.setAction("Com.Update.Services")
                        sendBroadcast(intent)
                        finish()
                    } else {
                        pd.dismiss()
                        toast("" + it.value.message)
                    }
                }
                is Resource.Loading -> {
                    pd.show()

                }
                is Resource.Failure -> {
                    pd.dismiss()
                    toast("" + it.exception!!.message)
                }
                is Resource.Empty -> {
                    pd.dismiss()
                }
            }
        })
    }


    private fun setspinner() {
        binding.spinnerRates.setItems(prioritylist)
        val typeface = ResourcesCompat.getFont(this, R.font.poppins)
        binding.spinnerRates.setTypeface(typeface)
        binding.spinnerRates.setOnItemSelectedListener(MaterialSpinner.OnItemSelectedListener<String> { view, position, id, item ->
            pricetype = item
        })

        binding.spinnerService.setItems(servicelist)
        binding.spinnerRates.setTypeface(typeface)
        binding.spinnerService.setOnItemSelectedListener(MaterialSpinner.OnItemSelectedListener<String> { view, position, id, item ->
            service = item
        })
    }


    private fun validations(): Boolean {
        if (binding.etPrice.text.toString().trim().isEmpty()) {
            binding.etPrice.error = "Empty"
            return false
        } else if (service.equals("")) {
            toast("Please Select Service")
            return false
        } else if (pricetype.equals("")) {
            toast("Please Select Price Type")
            return false
        } else if (binding.etBusdescription.text.toString().trim().isEmpty()) {
            binding.etBusdescription.error = "Empty"
            return false
        }
        return true
    }


    private fun getservices() {
        viewmodel.getservices()
        viewmodel.serivcedata.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        for (i in 0..it.value.getService.size - 1) {
                            servicelist.add(it.value.getService[i].serviceName)
                        }
                        setspinner()
                    }
                }
                is Resource.Failure -> {
                    toast("" + it.exception!!.message)
                }
                is Resource.Loading -> {

                }
            }
        })
    }

    override fun ondelete(pos: Int) {
        if (jsonArray.length() > 0) {
            jsonArray.remove(pos)
            adapter = SelectServiceAdapter(this, jsonArray, this)
            binding.recyclerview.adapter = adapter
        }
    }
}