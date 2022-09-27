package com.application.animalAlertApp.Fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.SharedPrefrences.MySharedPreferences
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.View.BusinessProfile.BusinessViewModel
import com.application.animalAlertApp.View.BusinessProfile.EditBussProfile.EditBusinessInfoActivity
import com.application.animalAlertApp.databinding.FragmentAboutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutFragment : Fragment() {
    private val viewmodel: BusinessViewModel by activityViewModels()
    private lateinit var binding: FragmentAboutBinding
    private lateinit var sharedPreferences: MySharedPreferences
    private var userid: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAboutBinding.inflate(layoutInflater, container, false)
        sharedPreferences = MySharedPreferences(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewmodel.businessdata.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        if (it.value.getShop.size > 0) {
                            userid = it.value.getShop[0].userId
                            if (!userid.equals(sharedPreferences.getUserid())) {
                                binding.btEdit.visibility = View.GONE
                            }
                            binding.tvBussinessname.setText("" + it.value.getShop[0].businessName)
                            binding.tvLocation.setText("" + it.value.getShop[0].location)
                            binding.tvMobile.setText("" + it.value.getShop[0].mobileNo)
                            binding.tvDescription.setText(""+it.value.getShop[0].businessDescription)
                        }
                    }
                }
                is Resource.Failure -> {
                    context?.toast("" + it.exception!!.message)
                }
                is Resource.Loading -> {

                }
            }
        })

        binding.btEdit.setOnClickListener {
            val intent = Intent(context, EditBusinessInfoActivity::class.java)
            intent.putExtra("name", binding.tvBussinessname.text.toString())
            intent.putExtra("location", binding.tvLocation.text.toString())
            intent.putExtra("mobile", binding.tvMobile.text.toString())
            intent.putExtra("description",binding.tvDescription.text.toString())
            startActivityForResult(intent, 1)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (data != null) {
                val name: String = data!!.getStringExtra("name").toString()
                val location: String = data.getStringExtra("location").toString()
                val mobile: String = data.getStringExtra("mobile").toString()
                val desc:String=data.getStringExtra("description").toString()
                binding.tvMobile.setText(mobile)
                binding.tvLocation.setText(location)
                binding.tvBussinessname.setText(name)
                binding.tvDescription.setText(desc)
            }

        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("About","Cancel")
          //  context?.toast("Cancelled About")
        }
    }


}