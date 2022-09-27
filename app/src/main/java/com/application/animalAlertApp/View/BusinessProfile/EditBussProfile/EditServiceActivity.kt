package com.application.animalAlertApp.View.BusinessProfile.EditBussProfile

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import com.application.animalAlertApp.Adapters.EditServiceAdapter
import com.application.animalAlertApp.Interfaces.ShopServiceInterface
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.R
import com.application.animalAlertApp.Utils.SelectPriority
import com.application.animalAlertApp.Utils.checkForInternet
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.data.Response.BusinessProfile.Service
import com.application.animalAlertApp.databinding.ActivityEditServiceBinding
import com.jaredrummler.materialspinner.MaterialSpinner
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class EditServiceActivity : AppCompatActivity(), ShopServiceInterface {
    private val viewmodel: EditServiceViewmodel by viewModels()
    private lateinit var binding: ActivityEditServiceBinding
    private var prioritylist = arrayListOf<String>("Hourly", "Weekly", "Monthly","One time")
    private var service: String = ""
    private var pricetype: String = ""
    private lateinit var servicelist: ArrayList<String>
    private var servicename: String? = null
    private var pricePeriod: String? = null
    private var serviceId: String? = null
    private lateinit var pd: Dialog
    private lateinit var service_list: ArrayList<Service>
    private lateinit var adapter: EditServiceAdapter
    private var selectedpos:Int=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        servicelist = ArrayList()
        service_list = ArrayList()
        pd = MyProgressBar.progress(this)


        val intent = intent
        val args = intent.getBundleExtra("BUNDLE")
        if (args != null) {
            service_list = args.getSerializable("service_list") as ArrayList<Service>
            if (service_list.size > 0) {

                serviceId=service_list[0]._id
                binding.etPrice.setText(service_list[0].price)
                binding.etBusdescription.setText(service_list[0].serviceDescription)

                adapter = EditServiceAdapter(this, service_list, this)
                binding.recyclerviewService.adapter = adapter

            }
        } else {
            val intent = intent
            if (intent.hasExtra("serviceId")) {
                serviceId = intent.getStringExtra("serviceId").toString()
                Log.e("serviceid", "" + serviceId)
                servicename = intent.getStringExtra("servicename").toString()
                val price = intent.getStringExtra("price").toString()
                val description = intent.getStringExtra("description").toString()
                pricePeriod = intent.getStringExtra("pricePeriod").toString()

                binding.etPrice.setText(price)
                binding.etBusdescription.setText(description)
            }
        }


        // setContentView(R.layout.activity_edit_service)
        getservices()
        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.btContinue.setOnClickListener {
            if (validations()) {
                if (checkForInternet(this)) {
                    viewmodel.editservices(
                        serviceId!!,
                        service,
                        pricetype,
                        binding.etPrice.text.toString(),
                        binding.etBusdescription.text.toString()
                    )
                    viewmodel.editserivcedata.observe(this, Observer {
                        when (it) {
                            is Resource.Success -> {
                                if (it.value.status == 200) {
                                    pd.dismiss()
                                    if (service_list.size == 0) {
                                        val innn = Intent()
                                        innn.putExtra("serviceId", it.value.data._id)
                                        innn.putExtra("servicename", it.value.data.service)
                                        innn.putExtra("price", it.value.data.price)
                                        innn.putExtra(
                                            "description",
                                            it.value.data.serviceDescription
                                        )
                                        innn.putExtra("pricePeriod", it.value.data.pricePeriod)
                                        setResult(4, innn)
                                        finish()
                                    }else {
                                        service_list.removeAt(selectedpos)
                                        service_list.add(0,it.value.data)
                                        adapter = EditServiceAdapter(this, service_list, this)
                                        binding.recyclerviewService.adapter = adapter

                                    }
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
                        }
                    })
                } else {
                    toast("No internet connection")
                }
            }
        }
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
                        pd.dismiss()
                    } else {
                        pd.dismiss()
                        toast("" + it.value.message)
                    }
                }
                is Resource.Failure -> {
                    toast("" + it.exception!!.message)
                    pd.dismiss()
                }
                is Resource.Loading -> {
                    pd.show()
                }
            }
        })
    }

    private fun setspinner() {
        binding.spinnerRates.setItems(prioritylist)
        val typeface = ResourcesCompat.getFont(this, R.font.poppins)
        binding.spinnerRates.setTypeface(typeface)
        binding.spinnerRates.setOnItemSelectedListener(MaterialSpinner.OnItemSelectedListener<String> { view, position, id, item ->
            // toast("" + item)
            pricetype = item
        })

        binding.spinnerService.setItems(servicelist)
        binding.spinnerRates.setTypeface(typeface)
        binding.spinnerService.setOnItemSelectedListener(MaterialSpinner.OnItemSelectedListener<String> { view, position, id, item ->
            //    toast("" + item)
            service = item
        })


        if (service_list.size == 0) {
            val priorityindex: Int = SelectPriority(servicelist, servicename!!)
            binding.spinnerService.selectedIndex = priorityindex
            service = servicelist[priorityindex]

            val priceindex: Int = SelectPriority(prioritylist, pricePeriod!!)
            binding.spinnerRates.selectedIndex = priceindex
            pricetype = prioritylist[priceindex]
        } else {
            val priorityindex: Int = SelectPriority(servicelist, service_list[0].service)
            binding.spinnerService.selectedIndex = priorityindex
            service = servicelist[priorityindex]

            val priceindex: Int = SelectPriority(prioritylist, service_list[0].pricePeriod)
            binding.spinnerRates.selectedIndex = priceindex
            pricetype = prioritylist[priceindex]
        }
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

    override fun ondelete(pos: Int) {
        selectedpos=pos
        binding.etPrice.setText(service_list[pos].price)
        binding.etBusdescription.setText(service_list[pos].serviceDescription)
        serviceId=service_list[pos]._id

        val priorityindex: Int = SelectPriority(servicelist, service_list[pos].service)
        binding.spinnerService.selectedIndex = priorityindex
        service = servicelist[priorityindex]

        val priceindex: Int = SelectPriority(prioritylist, service_list[pos].pricePeriod)
        binding.spinnerRates.selectedIndex = priceindex
        pricetype = prioritylist[priceindex]


    }

}