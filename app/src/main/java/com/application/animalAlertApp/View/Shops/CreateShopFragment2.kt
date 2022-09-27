package com.application.animalAlertApp.View.Shops


import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.application.animalAlertApp.Adapters.SelectServiceAdapter
import com.application.animalAlertApp.Interfaces.ShopServiceInterface
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.databinding.FragmentCreateShop2Binding
import com.jaredrummler.materialspinner.MaterialSpinner
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONObject
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.R
import kotlin.collections.ArrayList

@AndroidEntryPoint
class CreateShopFragment2 : Fragment(), ShopServiceInterface {
    private var service: String = ""
    private var pricetype: String = ""
    private val viewmodel: ShopViewModel by activityViewModels()
    private lateinit var binding: FragmentCreateShop2Binding
    private var prioritylist = arrayListOf<String>("Hourly", "Weekly", "Monthly","One time")
    private val mainjsonobject = JSONObject()
    private val jsonArray = JSONArray()
    private lateinit var adapter: SelectServiceAdapter
    private lateinit var servicelist: ArrayList<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCreateShop2Binding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        servicelist = ArrayList()

//
//        binding.recyclerview.layoutManager =
//            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)


        getservices()
        getbusinessdata()

        binding.btContinue.setOnClickListener {

            if (validations()) {
                val jsonservice = JSONObject()
                jsonservice.put("Service", service)
                jsonservice.put("price", binding.etPrice.text.toString().trim())
                jsonservice.put("pricePeriod", pricetype)
                jsonservice.put(
                    "serviceDescription",
                    binding.etBusdescription.text.toString().trim()
                )
                jsonArray.put(jsonservice)
                mainjsonobject.put("services", jsonArray)
                Log.e("json", "" + mainjsonobject)
                Addserivcestoshop()
            }

        }

        binding.imgAddmore.setOnClickListener {
            if (validations()) {
                addmorefunctionality()
            }
        }
    }


    fun addmorefunctionality() {
        val jsonservice = JSONObject()
        jsonservice.put("Service", service)
        jsonservice.put("price", binding.etPrice.text.toString().trim())
        jsonservice.put("pricePeriod", pricetype)
        jsonservice.put(
            "serviceDescription",
            binding.etBusdescription.text.toString().trim()
        )
        jsonArray.put(jsonservice)
        adapter = SelectServiceAdapter(requireContext(), jsonArray, this)
        binding.recyclerviewService.adapter = adapter

        binding.etBusdescription.setText("")
        binding.etPrice.setText("")
    }


    private fun Addserivcestoshop() {
        viewmodel.addshopservices(jsonArray.toString())
        val pd: Dialog = MyProgressBar.progress(requireContext())
        viewmodel.addserivcedata.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    pd.dismiss()
                    if (it.value.status == 200) {
                        val intent = Intent()
                        intent.setAction("ChangeSerrviceStatus")
                        context?.sendBroadcast(intent)
                        (getActivity() as CreateShopActivity?)!!.changescreens()
                    } else {
                        context?.toast("" + it.value.message)
                    }
                }
                is Resource.Loading -> {
                    pd.show()
                }
                is Resource.Failure -> {
                    pd.dismiss()
                    context?.toast("" + it.exception!!.message)
                }
                is Resource.Empty -> {
                    pd.dismiss()
                }
            }
        })
    }


    private fun setspinner() {
        binding.spinnerRates.setItems(prioritylist)
        val typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins)
        binding.spinnerRates.setTypeface(typeface)
        binding.spinnerRates.setOnItemSelectedListener(MaterialSpinner.OnItemSelectedListener<String> { view, position, id, item ->
            //  context?.toast("" + item)
            pricetype = item
        })

        binding.spinnerService.setItems(servicelist)
        binding.spinnerRates.setTypeface(typeface)
        binding.spinnerService.setOnItemSelectedListener(MaterialSpinner.OnItemSelectedListener<String> { view, position, id, item ->
            //   context?.toast("" + item)
            service = item
        })
    }


    private fun validations(): Boolean {
        if (binding.etPrice.text.toString().trim().isEmpty()) {
            binding.etPrice.error = "Empty"
            return false
        } else if (service.equals("")) {
            context?.toast("Please Select Service")
            return false
        } else if (pricetype.equals("")) {
            context?.toast("Please Select Price Type")
            return false
        } else if (binding.etBusdescription.text.toString().trim().isEmpty()) {
            binding.etBusdescription.error = "Empty"
            return false
        }
        return true
    }

    private fun getbusinessdata() {
        viewmodel.businessdata.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        if (it.value.getShop.size > 0) {
                            if (it.value.getShop[0].service.size > 0) {
                                binding.etPrice.setText(it.value.getShop[0].service[0].price)
                                binding.etBusdescription.setText(it.value.getShop[0].service[0].serviceDescription)
                                binding.spinnerRates.selectedIndex = 0
                                binding.spinnerService.selectedIndex = 0
                            }
                        }
                    } else {
                        context?.toast("" + it.value.message)
                    }
                }
                is Resource.Failure -> {
                    context?.toast("" + it.exception!!.message)
                }
                is Resource.Loading -> {

                }
            }
        })
    }


    private fun getservices() {
        viewmodel.getservices()
        viewmodel.serivcedata.observe(viewLifecycleOwner, Observer {
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
                    context?.toast("" + it.exception!!.message)
                }
                is Resource.Loading -> {

                }
            }
        })
    }


    override fun ondelete(pos: Int) {
        if (jsonArray.length() > 0) {
            jsonArray.remove(pos)
            adapter = SelectServiceAdapter(requireContext(), jsonArray, this)
            binding.recyclerviewService.adapter = adapter
        }
    }
}