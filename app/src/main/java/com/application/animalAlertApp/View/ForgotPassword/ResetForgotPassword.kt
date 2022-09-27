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
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.databinding.FragmentResetForgotPasswordBinding
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 * Use the [ResetForgotPassword.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class ResetForgotPassword : Fragment() {
    private lateinit var binding: FragmentResetForgotPasswordBinding
    private var id: String = ""
    private val viewmodel: ForgotviewModel by viewModels()
    private lateinit var pd:Dialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentResetForgotPasswordBinding.inflate(layoutInflater, container, false)
         pd = MyProgressBar.progress(requireContext())
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        id = arguments?.getString("id").toString()


        binding.btVerify.setOnClickListener {
            if (validation()) {
                doresetpassword()
            }
        }

        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun doresetpassword() {
        viewmodel.updatepassword(
            id,
            binding.etPassword.text.toString().trim(),
            binding.etConpassword.text.toString().trim()
        )

        viewmodel.updatepassword.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        pd.dismiss()
                        activity?.finish()
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
        val password = binding.etPassword.text.toString().trim()
        if (binding.etPassword.text.toString().trim().isEmpty()) {
            binding.etPassword.error = "Empty"
            return false
        } else if (!password.equals(binding.etConpassword.text.toString().trim())) {
            binding.etConpassword.error = "Empty"
            return false
        }
        return true
    }


}