package com.application.animalAlertApp.Fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.application.animalAlertApp.Adapters.ServiceAdapter
import com.application.animalAlertApp.Interfaces.ChangeServicesInterface
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.View.BusinessProfile.BusinessViewModel
import com.application.animalAlertApp.View.BusinessProfile.EditBussProfile.EditServiceActivity
import com.application.animalAlertApp.data.Response.BusinessProfile.Service
import com.application.animalAlertApp.databinding.FragmentServiceBinding
import android.app.Activity.RESULT_CANCELED
import android.app.Dialog
import android.util.Log
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.SharedPrefrences.MySharedPreferences
import com.application.animalAlertApp.Utils.checkForInternet
import com.application.animalAlertApp.View.BusinessProfile.EditBussProfile.AddMoreServiceActivity


class ServiceFragment : Fragment(), ChangeServicesInterface {
    private val viewmodel: BusinessViewModel by activityViewModels()
    private lateinit var adapter: ServiceAdapter
    private lateinit var binding: FragmentServiceBinding
    private lateinit var list: ArrayList<Service>
    private lateinit var sharedPreferences: MySharedPreferences
    private var userid: String = ""
    private var position: Int = 0
    private lateinit var pd: Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentServiceBinding.inflate(layoutInflater, container, false)
        sharedPreferences = MySharedPreferences(requireContext())
        pd = MyProgressBar.progress(requireContext())
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list = ArrayList()

        viewmodel.businessdata.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        list.clear()
                        if (it.value.getShop.size > 0) {
                            userid = it.value.getShop[0].userId
                            if (!userid.equals(sharedPreferences.getUserid())) {
                                binding.addMore.visibility = View.GONE
                            }
                            list.addAll(it.value.getShop[0].service)
                            if (list.size > 0) {
                            binding.tvNoalert.visibility=View.GONE
                            } else {
                                binding.tvNoalert.visibility=View.VISIBLE
                            }
                            adapter = ServiceAdapter(
                                context,
                                list,
                                this,
                                userid,
                                sharedPreferences.getUserid()!!
                            )
                            binding.recyclerview.adapter = adapter
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


        binding.addMore.setOnClickListener {
            val intent = Intent(context, AddMoreServiceActivity::class.java)
            startActivity(intent)
        }


        removeservice()
    }

    private fun removeservice() {
        viewmodel.deleteservice.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        pd.dismiss()
                        context?.toast("" + it.value.message)
                        list.removeAt(position)
                        adapter = ServiceAdapter(
                            context,
                            list,
                            this,
                            userid,
                            sharedPreferences.getUserid()!!
                        )
                        binding.recyclerview.adapter = adapter
                    } else {
                        pd.dismiss()
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
            }
        })
    }

    override fun onedit(pos: Int, service: Service) {
        val intent = Intent(context, EditServiceActivity::class.java)
        intent.putExtra("serviceId", service._id)
        intent.putExtra("servicename", service.service)
        intent.putExtra("price", service.price)
        intent.putExtra("description", service.serviceDescription)
        intent.putExtra("pricePeriod", service.pricePeriod)
        startActivityForResult(intent, 4)
    }

    override fun ondelete(pos: Int, serviceid: String) {
        position = pos
        deleteservice(serviceid)
    }


    private fun deleteservice(id: String) {
        if (checkForInternet(requireContext())) {
            viewmodel.deleteservice(id)
        } else {
            context?.toast("No internet connection")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 4) {
            if (data != null) {
                val serviceId: String = data.getStringExtra("serviceId").toString()
                val servicename: String = data.getStringExtra("servicename").toString()
                val price: String = data.getStringExtra("price").toString()
                val description: String = data.getStringExtra("description").toString()
                val pricePeriod: String = data.getStringExtra("pricePeriod").toString()

                for (i in 0..list.size - 1) {
                    if (list[i]._id.equals(serviceId)) {
                        list.removeAt(i)
                        val servicemodel = Service(
                            0,
                            serviceId,
                            "",
                            price,
                            servicename,
                            description,
                            "",
                            "",
                            "",
                            pricePeriod
                        )
                        list.add(servicemodel)
                    }
                }
                adapter =
                    ServiceAdapter(context, list, this, userid, sharedPreferences.getUserid()!!)
                binding.recyclerview.adapter = adapter
            }
        } else if (resultCode == RESULT_CANCELED) {
            Log.e("Service", "Cancel")
            // context?.toast("Cancelled Edit")
        }

    }
}