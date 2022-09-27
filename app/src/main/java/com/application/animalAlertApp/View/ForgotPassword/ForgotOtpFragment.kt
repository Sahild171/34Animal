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
import com.application.animalAlertApp.databinding.FragmentForgotOtpBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotOtpFragment : Fragment() {
    private lateinit var binding: FragmentForgotOtpBinding
    private val viewmodel: ForgotviewModel by viewModels()
    private lateinit var pd: Dialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentForgotOtpBinding.inflate(layoutInflater, container, false)
        pd = MyProgressBar.progress(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btContinue.setOnClickListener {
            if (validation()) {
                if (checkForInternet(requireContext())) {
                    dogenertateotp()
                } else {
                    context?.toast("No internet connection")
                }
            }
        }

        binding.imgBack.setOnClickListener {
            activity?.finish()
        }
    }

    private fun dogenertateotp() {
        viewmodel.forgotpassword(binding.etEmail.text.toString().trim())
        viewmodel.forgotreponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        pd.dismiss()
                        val bundle = Bundle()
                        bundle.putString("userid", it.value.userId)
                        bundle.putString("otp", it.value.otp)
                        findNavController().navigate(R.id.action_forgotOtpFragment_to_verifyForgotOtp, bundle)
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
        if (binding.etEmail.text.toString().trim().isEmpty()) {
            binding.etEmail.error = "Empty"
            return false
        }
        return true
    }
}