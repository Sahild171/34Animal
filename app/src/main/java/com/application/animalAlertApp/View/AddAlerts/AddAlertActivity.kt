package com.application.animalAlertApp.View.AddAlerts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.application.animalAlertApp.R
import com.application.animalAlertApp.Utils.CustomSpinnerAdapter
import com.application.animalAlertApp.Utils.SelectPriority
import com.application.animalAlertApp.Utils.SelectmyPet
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.View.Pet.AddPetActivity
import com.application.animalAlertApp.databinding.ActivityAddAlertBinding
import com.jaredrummler.materialspinner.MaterialSpinner
import com.kaopiz.kprogresshud.KProgressHUD
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import java.io.Serializable


@AndroidEntryPoint
class AddAlertActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddAlertBinding
    private val viewModel: AddAlertViewModel by viewModels()
    private lateinit var petlist: ArrayList<String>
    private lateinit var petid: ArrayList<String>
    private var prioritylist = arrayListOf<String>("LOW", "MEDIUM", "HIGH")
    private var prioritylistcolor = arrayListOf<Int>(R.mipmap.green, R.mipmap.yellow, R.mipmap.red)
    private var selectedpet: String? = null
    private var selectedpetid: String? = ""
    private var priority: String? = null
    private lateinit var progress: KProgressHUD
    private lateinit var screentype: String
    private lateinit var alertid: String

    private var petname = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_add_alert)
        binding = ActivityAddAlertBinding.inflate(layoutInflater)
        screentype = intent.getStringExtra("ScreenType").toString()
        alertid = intent.getStringExtra("alertid").toString()
        setContentView(binding.root)
        petlist = ArrayList()
        petid = ArrayList()
        selectedpetid = null

        if (screentype.equals("Edit")) {
            petname = intent.getStringExtra("mypetname").toString()
            if (petname.isNullOrEmpty()) {
                binding.conPet.visibility = View.GONE
                binding.spinnerPets.visibility = View.GONE
                binding.imgPet.visibility = View.GONE
            }
            binding.tvTitle.setText("Edit Alert")
            binding.btPost.setText("Save")
            binding.tvAddpet.visibility = View.GONE
        }

        progress = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait")

        setPrioritySpinners()
        getMyPets()
        AddAlert()
    }

    private fun editmyAlert() {
        val title = intent.getStringExtra("title")
       // val petname = intent.getStringExtra("mypetname")
        val priorityyy = intent.getStringExtra("priority")
        val description = intent.getStringExtra("description")
        binding.etPetname.setText(title)
        binding.etDescription.setText(description)
        if (screentype.equals("Edit")) {
            if (petlist.size > 0) {
                val petindex: Int = SelectmyPet(petlist, petname)
                binding.spinnerPets.selectedIndex = petindex
                selectedpetid = petid[petindex]
            }
            if (petname.isNullOrEmpty()){
                selectedpetid=null
            }
            val priorityindex: Int = SelectPriority(prioritylist, priorityyy!!)
            binding.spinnerPriority.setSelection(priorityindex)
            priority = prioritylist[priorityindex]
        }
    }


    override fun onRestart() {
        super.onRestart()
        getMyPets()
    }

    private fun validations(): Boolean {
        if (binding.etPetname.text.toString().isEmpty()) {
            toast("Enter title")
            return false
        } else if (priority.isNullOrEmpty()) {
            toast("Please Selected Priority")
            return false
        } else if (binding.etDescription.text.toString().isEmpty()) {
            toast("Enter Description")
            return false
        }
        return true
    }

    private fun AddAlert() {
        binding.apply {
            btPost.setOnClickListener {
                if (validations()) {
                    if (screentype.equals("Edit")) {
                        EditAlertApi()
                    } else {
                        AddAlertApi()
                    }
                }
            }
        }
    }

    private fun AddAlertApi() {
        val desc: String = binding.etDescription.text.toString().trim()
        lifecycleScope.launchWhenStarted {
            viewModel.addalert(
                binding.etPetname.text.toString().trim(),
                priority!!,
                desc,
                "pending",
                selectedpetid
            ).catch { e ->
                toast("" + e.message)
                progress.dismiss()
            }.onStart {
                progress.show()
            }.collect { response ->
                if (response.status == 200) {
                    progress.dismiss()
                    finish()
                } else {
                    toast("" + response.message)
                    progress.dismiss()
                }
            }
        }
    }


    private fun EditAlertApi() {
        lifecycleScope.launchWhenStarted {
            viewModel.editalert(
                alertid,
                binding.etPetname.text.toString().trim(),
                selectedpetid,
                priority!!,
                binding.etDescription.text.toString().trim()
            ).catch { e ->
                toast("" + e.message)
                progress.dismiss()
            }.onStart {
                progress.show()
            }.collect { response ->
                if (response.status == 200) {
                    progress.dismiss()
                    val intent = Intent()
                    intent.putExtra("edit_alert", response.updateAlert as Serializable)
                    setResult(1, intent)
                    finish()
                } else {
                    progress.dismiss()
                }
            }
        }
    }

    private fun getMyPets() {
        viewModel.getpets()
        lifecycleScope.launchWhenStarted {
            viewModel.mypetData.collect {
                binding.apply {
                    when (it) {
                        is MyPetsState.Success -> {
                            petid.clear()
                            petlist.clear()

                            for (i in 0..it.data.findPet.size - 1) {
                                petlist.add(it.data.findPet[i].petName)
                                //  Log.e("pets", "" + it.data.findPet[i].petName)
                                petid.add(it.data.findPet[i]._id)
                            }
                            setpetspinner(petlist, petid)
                        }
                        is MyPetsState.Failure -> {
                            toast("" + it.msg)
                            Log.e("mypet", "" + it.msg)
                        }
                        is MyPetsState.Loading -> {
                            //    progress.show()
                        }
                        is MyPetsState.Empty -> {

                        }
                    }
                }
            }
        }
    }

    private fun setpetspinner(petlist: ArrayList<String>, petidlist: ArrayList<String>) {
        binding.spinnerPets.setItems(petlist)
        binding.spinnerPets.setOnItemSelectedListener(MaterialSpinner.OnItemSelectedListener<String> { view, position, id, item ->
            selectedpet = item
            selectedpetid = petidlist[position]
            //   toast("" + selectedpetid)
        })
        editmyAlert()

    }

    private fun setPrioritySpinners() {

        val adapter = CustomSpinnerAdapter(
            this,
            prioritylist, prioritylistcolor
        )
        binding.spinnerPriority.adapter = adapter

        binding.spinnerPriority.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                priority = prioritylist[pos]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })


//        binding.spinnerPriority.setItems(prioritylist)
//        binding.spinnerPriority.setOnItemSelectedListener(MaterialSpinner.OnItemSelectedListener<String> { view, position, id, item ->
//            // toast("" + item)
//            priority = item
//        })
//        binding.spinnerPriority.setOnNothingSelectedListener(MaterialSpinner.OnNothingSelectedListener { spinner ->
//
//        })

    }

    fun Close(view: android.view.View) {
        finish()
    }

    fun AddPet(view: android.view.View) {
        val intent = Intent(this, AddPetActivity::class.java)
        intent.putExtra("Screentype", "AddAlert")
        startActivity(intent)
    }
}