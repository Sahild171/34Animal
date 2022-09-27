package com.application.animalAlertApp.View.Alerts

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.application.animalAlertApp.Adapters.AlertAdapter
import com.application.animalAlertApp.Interfaces.AlertReport
import com.application.animalAlertApp.Interfaces.AlertsOptionInterface
import com.application.animalAlertApp.Model.AlertModel
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.SharedPrefrences.MySharedPreferences
import com.application.animalAlertApp.Utils.MyDialog
import com.application.animalAlertApp.Utils.checkForInternet
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.View.AddAlerts.AddAlertActivity
import com.application.animalAlertApp.View.AlertDetail.ShopDetailActivity
import com.application.animalAlertApp.View.Chats.ChatActivity
import com.application.animalAlertApp.View.Chats.QuestionsActivity
import com.application.animalAlertApp.data.Response.MessageX
import com.application.animalAlertApp.databinding.FragmentAlertBinding
import com.google.android.gms.ads.*
import com.kaopiz.kprogresshud.KProgressHUD
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart


@AndroidEntryPoint
class AlertFragment : Fragment() {
    var arraylist = ArrayList<AlertModel>()
    private lateinit var adapter: AlertAdapter
    private lateinit var binding: FragmentAlertBinding
    private val viewmodel: AlertViewModel by viewModels()
    private lateinit var progress: KProgressHUD
    private lateinit var mySharedPreferences: MySharedPreferences
    private var editposition = 0
    private lateinit var list: ArrayList<MessageX>
    private lateinit var selectedlist: ArrayList<MessageX>
    private var aler_id: String = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list = ArrayList()
        selectedlist = ArrayList()

        progress = KProgressHUD.create(activity)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please Wait")

        mySharedPreferences = MySharedPreferences(requireContext())

        adapter = AlertAdapter(context, ArrayList(), inteface)
        binding.swipeRefresh.setOnRefreshListener(refresh)

        getAlertsData()
        getchatscreenobserver()
        ads()
        binding.closeadd.setOnClickListener {
            binding.adView.visibility=View.GONE
            binding.closeadd.visibility=View.GONE
        }
    }


    private fun ads() {
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(requireContext()) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)


        binding.adView.adListener = object: AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                Log.e("Admob",""+adError.message)
                // Code to be executed when an ad request fails.
            }

            override fun onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdOpened() {

                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        }
    }

    private fun getchatscreenobserver() {
        viewmodel.screenData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        if (it.value.message.equals("you already submitted the answer")) {
                            progress.dismiss()
                            val intent = Intent(context, ChatActivity::class.java)
                            intent.putExtra("ReceverId", selectedlist[0].userId)
                            intent.putExtra("ReceverName", selectedlist[0].userName)
                            intent.putExtra("ReceverImage", selectedlist[0].userImage)
                            intent.putExtra("ChatRoom", getchatroom(mySharedPreferences.getUserid()!!, selectedlist[0].userId))
                            intent.putExtra("devicetoken", selectedlist[0].deviceToken)
                            startActivity(intent)
                        } else {
                            progress.dismiss()
                            val intent = Intent(context, QuestionsActivity::class.java)
                            intent.putExtra("ReceverId", selectedlist[0].userId)
                            intent.putExtra("ReceverName", selectedlist[0].userName)
                            intent.putExtra("ReceverImage", selectedlist[0].userImage)
                            intent.putExtra("ChatRoom", getchatroom(mySharedPreferences.getUserid()!!, selectedlist[0].userId))
                            intent.putExtra("alertid", aler_id)
                            intent.putExtra("devicetoken", selectedlist[0].deviceToken)
                            context?.startActivity(intent)
                        }
                    } else {
                        progress.dismiss()
                        context?.toast("" + it.value.message)
                    }
                }
                is Resource.Failure -> {
                    context?.toast("" + it.exception!!.message)
                    progress.dismiss()
                }
                is Resource.Loading -> {
                    progress.show()
                }
            }
        })
    }


    private val refresh = object : SwipeRefreshLayout.OnRefreshListener {
        override fun onRefresh() {
            if (checkForInternet(requireContext())) {
                viewmodel.getAllData()
            } else {
                context?.toast("No Internet Connection")
            }
        }
    }


    private fun getAlertsData() {
        if (checkForInternet(requireContext())) {
            viewmodel.getAllData()
        } else {
            context?.toast("No Internet Connection")
        }

        viewmodel.alertData.observe(requireActivity(), Observer {
            binding.apply {
                when (it) {
                    is Resource.Success -> {
                        // progress.dismiss()
                        if (it.value.status == 200) {
                            if (it.value.findAlert.size > 0) {
                                list.clear()
                                list.addAll(it.value.findAlert)
                                adapter.setData(list)
                                recyclerview.adapter = adapter
                            }
                        } else {
                            context?.toast("" + it.value.message)
                        }
                        shimmer.stopShimmer()
                        shimmer.visibility = View.INVISIBLE
                        swipeRefresh.isRefreshing = false
                    }
                    is Resource.Failure -> {
                        shimmer.stopShimmer()
                        Toast.makeText(context, "" + it.exception!!.message, Toast.LENGTH_SHORT)
                            .show()
                        Log.d("main", "getBusData: ${it.exception.message} ")
                        swipeRefresh.isRefreshing = false
                    }
                    is Resource.Loading -> {
                        shimmer.startShimmer()
                        swipeRefresh.isRefreshing = false
                    }

                }
            }
        })
    }


    private val inteface = object : AlertsOptionInterface {
        override fun onEdit(
            pos: Int,
            alertId: String,
            title: String,
            petname: String,
            priority: String,
            discription: String,
            petid: String
        ) {
            editposition = pos
            val intent = Intent(context, AddAlertActivity::class.java)
            intent.putExtra("ScreenType", "Edit")
            intent.putExtra("title", title)
            intent.putExtra("alertid", alertId)
            intent.putExtra("mypetname", petname)
            intent.putExtra("priority", priority)
            intent.putExtra("description", discription)
            intent.putExtra("petid", petid)
            startActivityForResult(intent, 1)
        }
        override fun OnClosePost(pos: Int, alertId: String) {
            closeAlert(alertId)
        }
        override fun OnDelete(pos: Int, alertId: String) {
            deleteMyAlerts(alertId)
        }
        override fun OnChat(pos: Int, alertId: String, list: MessageX) {
            aler_id = alertId
            selectedlist.add(list)
            if (checkForInternet(requireContext())) {
                viewmodel.getscreen(alertId)
            } else {
                context?.toast("No Internet Connection")
            }
        }
        override fun OnReport(pos: Int, alertId: String) {
            val dialog = MyDialog()
            dialog.show_report_dialog(context, report, alertId)
        }

        override fun OnDetail(alertId: String,name:String) {
            val intent = Intent(context, ShopDetailActivity::class.java)
            intent.putExtra("alertid", alertId)
            intent.putExtra("username",name)
            startActivity(intent)
        }
    }

    private fun closeAlert(alertId: String) {
        lifecycleScope.launchWhenStarted {
            viewmodel.closealert(alertId, false).catch { e ->
                activity?.toast("" + e.message)
            }.onStart {

            }.collect { data ->
                if (data.status == 200) {
                    getAlertsData()
                } else {
                    activity?.toast("" + data.message)
                }
            }
        }
    }

    private fun reportAlert(Alertid: String, reportBy: String, reason: String) {
        viewmodel.reportAlert(Alertid, reportBy, reason)
        val pd: Dialog = MyProgressBar.progress(context)
        viewmodel.reportData.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    pd.dismiss()
                    if (it.value.status == 200) {
                        context?.toast("" + it.value.message)
                    } else {
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

    val report = object : AlertReport {
        override fun onDoReport(alertid: String, reson: String) {
            reportAlert(alertid, mySharedPreferences.getUserid()!!, reson)
        }
    }

    private fun deleteMyAlerts(alertId: String) {
        lifecycleScope.launchWhenStarted {
            viewmodel.deletealert(alertId).catch { e ->
                activity?.toast("" + e.message)
            }.onStart {

            }.collect { data ->
                if (data.status == 200) {
                    getAlertsData()
                } else {
                    activity?.toast("" + data.message)
                }
            }
        }
    }

    private fun getchatroom(myid: String, otherid: String): String {
        val chatRoom: String
        chatRoom = if (myid.compareTo(otherid) < 0) {
            myid + "_" + otherid
        } else {
            otherid + "_" + myid
        }
        return chatRoom
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (data != null) {
                val model = data.extras?.get("edit_alert") as MessageX
                list.removeAt(editposition)
                list.add(editposition, model)
                adapter.setData(list)
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("About", "Cancel")
            //  context?.toast("Cancelled About")
        }
    }

}