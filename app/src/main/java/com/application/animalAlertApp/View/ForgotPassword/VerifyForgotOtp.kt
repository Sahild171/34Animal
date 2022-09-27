package com.application.animalAlertApp.View.ForgotPassword

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.R
import com.application.animalAlertApp.Utils.checkForInternet
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.databinding.FragmentVerifyForgotOtpBinding
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 * Use the [VerifyForgotOtp.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class VerifyForgotOtp : Fragment() {
    private lateinit var binding: FragmentVerifyForgotOtpBinding
    private val viewmodel: ForgotviewModel by viewModels()
    private var userid: String = ""
    private var otp: String = ""
    private lateinit var pd: Dialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVerifyForgotOtpBinding.inflate(layoutInflater, container, false)
        pd = MyProgressBar.progress(requireContext())
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userid = arguments?.getString("userid").toString()
        otp = arguments?.getString("otp").toString()
        binding.etOtp.setText(otp)


        binding.btVerify.setOnClickListener {
            if (validation()) {
                if (checkForInternet(requireContext())) {
                    doverify()
                } else {
                    context?.toast("No internet connection")
                }
            }
        }


        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun doverify() {
        viewmodel.verifyotp(userid, otp)
        viewmodel.verifyreponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        pd.dismiss()
                        val bundle = Bundle()
                        bundle.putString("id", it.value._id)
                        findNavController().navigate(
                            R.id.action_verifyForgotOtp_to_resetForgotPassword,
                            bundle)
                    } else {
                        pd.dismiss()
                        context?.toast("" + it.value.message)
                    }
                }
                is Resource.Failure -> {
                    pd.dismiss()
                    context?.toast("" + it.exception!!.message)
                }
                is Resource.Loading -> {
                    pd.show()
                }
            }
        })
    }


    private fun validation(): Boolean {
        if (binding.etOtp.text.toString().trim().isEmpty()) {
            binding.etOtp.error = "Empty"
            return false
        }
        return true
    }

}