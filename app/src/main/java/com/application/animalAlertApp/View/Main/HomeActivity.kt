package com.application.animalAlertApp.View.Main

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.application.animalAlertApp.Interfaces.LogoutInterface
import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.ProgressBar.MyProgressBar
import com.application.animalAlertApp.R
import com.application.animalAlertApp.SharedPrefrences.MySharedPreferences
import com.application.animalAlertApp.SharedPrefrences.UserPreferences
import com.application.animalAlertApp.Utils.MyDialog
import com.application.animalAlertApp.Utils.checkForInternet
import com.application.animalAlertApp.Utils.toast
import com.application.animalAlertApp.View.AddAlerts.AddAlertActivity
import com.application.animalAlertApp.View.Auth.LoginActivity
import com.application.animalAlertApp.View.BusinessProfile.BusinessProfileActivity
import com.application.animalAlertApp.View.Payments.CardPaymentActivity
import com.application.animalAlertApp.View.ContactUsActivity
import com.application.animalAlertApp.View.FaqActivity
import com.application.animalAlertApp.databinding.ActivityHomeBinding
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import com.application.animalAlertApp.View.Profile.MyProfileActivity
import com.application.animalAlertApp.View.Settings.SettingsActivity
import com.application.animalAlertApp.View.TermsandConditions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.getkeepsafe.taptargetview.TapTargetView
import android.graphics.Typeface
import com.getkeepsafe.taptargetview.TapTarget


@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var navController: NavController? = null
    private var img_profile: CircleImageView? = null
    private var tv_name: TextView? = null
    private var tabselect: String? = null
    private var notificationclick: String? = null
    private lateinit var mySharedPreferences: MySharedPreferences
    private lateinit var userPreferences: UserPreferences
    private lateinit var binding: ActivityHomeBinding
    private val viewmodel: MainViewmodel by viewModels()
    private lateinit var pd: Dialog
    private lateinit var circle_progressview: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.activity_home)
        tabselect = intent.getStringExtra("Screentype")
        notificationclick = intent.getStringExtra("Screeentype")
        mySharedPreferences = MySharedPreferences(this)
        userPreferences = UserPreferences(this)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        pd = MyProgressBar.progress(this)
        setContentView(binding.root)
        intialize()

        binding.fab.setOnClickListener {
            navController?.navigate(R.id.homeFragment)
        }

        getshopdata()

        if (notificationclick.equals("Chatscreen")) {
            navController?.navigate(R.id.chatFragment)
        } else if (notificationclick.equals("AlertScreen")) {
            navController?.navigate(R.id.alertFragment)
        }

        binding.drawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                Log.e("drawer", "slide")
            }

            override fun onDrawerOpened(drawerView: View) {
                draweropen()
            }

            override fun onDrawerClosed(drawerView: View) {
                Log.e("drawer", "Closed")
            }

            override fun onDrawerStateChanged(newState: Int) {
                Log.e("drawer", "Statechanged")
            }
        })
        if (tabselect!=null) {
            if (tabselect.equals("Pet"))
            if (mySharedPreferences.getshowfirsttime()) {
                settargetview()
            }
        }
    }

    private fun settargetview() {
        TapTargetView.showFor(this,
            TapTarget.forView(
                binding.imgMenu,
                " Find your own profile pages,\n business pages and much\n more here!",
                "")
                .outerCircleColor(R.color.green) // Specify a color for the outer circle
                .outerCircleAlpha(0.96f) // Specify the alpha amount for the outer circle
                .targetCircleColor(R.color.white) // Specify a color for the target circle
                .titleTextSize(20) // Specify the size (in sp) of the title text
                .titleTextColor(R.color.white) // Specify the color of the title text
                .descriptionTextSize(10) // Specify the size (in sp) of the description text
                .descriptionTextColor(R.color.white) // Specify the color of the description text
                .textColor(R.color.white) // Specify a color for both the title and description text
                .textTypeface(Typeface.SANS_SERIF) // Specify a typeface for the text
                .dimColor(R.color.black) // If set, will dim behind the view with 30% opacity of the given color
                .drawShadow(true) // Whether to draw a drop shadow or not
                .cancelable(false) // Whether tapping outside the outer circle dismisses the view
                .tintTarget(true) // Whether to tint the target view's color
                .transparentTarget(false)
                .icon(resources.getDrawable(R.drawable.menu_icon))// Specify whether the target is transparent (displays the content underneath)
                // Specify a custom drawable to draw as the target
                .targetRadius(60),  // Specify the target radius (in dp)
            object : TapTargetView.Listener() {
                // The listener can listen for regular clicks, long clicks or cancels
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view) // This call is optional
                    settargetpost()
                }
            })
    }

    private fun settargetpost() {
        TapTargetView.showFor(this,  // `this` is an Activity
            TapTarget.forView(
                binding.imgAddInstruction,
                "Add your own Post here!",
                ""
            ) // All options below are optional
                .outerCircleColor(R.color.green) // Specify a color for the outer circle
                .outerCircleAlpha(0.96f) // Specify the alpha amount for the outer circle
                .targetCircleColor(R.color.white) // Specify a color for the target circle
                .titleTextSize(20) // Specify the size (in sp) of the title text
                .titleTextColor(R.color.white) // Specify the color of the title text
                .descriptionTextSize(10) // Specify the size (in sp) of the description text
                .descriptionTextColor(R.color.white) // Specify the color of the description text
                .textColor(R.color.white) // Specify a color for both the title and description text
                .textTypeface(Typeface.SANS_SERIF) // Specify a typeface for the text
                .dimColor(R.color.black) // If set, will dim behind the view with 30% opacity of the given color
                .drawShadow(true) // Whether to draw a drop shadow or not
                .cancelable(false) // Whether tapping outside the outer circle dismisses the view
                .tintTarget(true) // Whether to tint the target view's color
                .transparentTarget(false)
                .icon(resources.getDrawable(R.drawable.white_add)) // Specify whether the target is transparent (displays the content underneath)
                // Specify a custom drawable to draw as the targetm
                .targetRadius(60),  // Specify the target radius (in dp)
            object : TapTargetView.Listener() {
                // The listener can listen for regular clicks, long clicks or cancels
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view) // This call is optional
                    settargetalert()
                }
            })
    }

    private fun settargetalert() {
        TapTargetView.showFor(this,  // `this` is an Activity
            TapTarget.forView(
                binding.imgMenuAlert,
                " Tap here for important alerts \n from your community,and post\n your own as well!",
                ""
            ) // All options below are optional
                .outerCircleColor(R.color.green) // Specify a color for the outer circle
                .outerCircleAlpha(0.96f) // Specify the alpha amount for the outer circle
                .targetCircleColor(R.color.white) // Specify a color for the target circle
                .titleTextSize(20) // Specify the size (in sp) of the title text
                .titleTextColor(R.color.white) // Specify the color of the title text
                .descriptionTextSize(10) // Specify the size (in sp) of the description text
                .descriptionTextColor(R.color.white) // Specify the color of the description text
                .textColor(R.color.white) // Specify a color for both the title and description text
                .textTypeface(Typeface.SANS_SERIF) // Specify a typeface for the text
                .dimColor(R.color.black) // If set, will dim behind the view with 30% opacity of the given color
                .drawShadow(true) // Whether to draw a drop shadow or not
                .cancelable(false) // Whether tapping outside the outer circle dismisses the view
                .tintTarget(true) // Whether to tint the target view's color
                .transparentTarget(false)
                .icon(resources.getDrawable(R.mipmap.alertde)) // Specify whether the target is transparent (displays the content underneath)
                // Specify a custom drawable to draw as the target
                .targetRadius(60),  // Specify the target radius (in dp)
            object : TapTargetView.Listener() {
                // The listener can listen for regular clicks, long clicks or cancels
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view) // This call is optional
                    settargetshop()
                }
            })
    }


    private fun settargetshop() {
        TapTargetView.showFor(this,  // `this` is an Activity
            TapTarget.forView(
                binding.imgMenuShop,
                " Create a shop here or browse\n the other businesses",
                ""
            ) // All options below are optional
                .outerCircleColor(R.color.green) // Specify a color for the outer circle
                .outerCircleAlpha(0.96f) // Specify the alpha amount for the outer circle
                .targetCircleColor(R.color.white) // Specify a color for the target circle
                .titleTextSize(20) // Specify the size (in sp) of the title text
                .titleTextColor(R.color.white) // Specify the color of the title text
                .descriptionTextSize(10) // Specify the size (in sp) of the description text
                .descriptionTextColor(R.color.white) // Specify the color of the description text
                .textColor(R.color.white) // Specify a color for both the title and description text
                .textTypeface(Typeface.SANS_SERIF) // Specify a typeface for the text
                .dimColor(R.color.black) // If set, will dim behind the view with 30% opacity of the given color
                .drawShadow(true) // Whether to draw a drop shadow or not
                .cancelable(false) // Whether tapping outside the outer circle dismisses the view
                .tintTarget(true) // Whether to tint the target view's color
                .transparentTarget(false)
                .icon(resources.getDrawable(R.mipmap.shopde)) // Specify whether the target is transparent (displays the content underneath)
                .targetRadius(60),  // Specify the target radius (in dp)
            object : TapTargetView.Listener() {
                // The listener can listen for regular clicks, long clicks or cancels
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view) // This call is optional
                    mySharedPreferences.setshowfirsttime(false)
                }
            })
    }


    private fun getshopdata() {
        viewmodel.checkshopdata.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 200) {
                        pd.dismiss()
                        if (it.value.shopStatus.equals(1)) {
                            val intent = Intent(this, BusinessProfileActivity::class.java)
                            startActivity(intent)
                        } else {
                            toast("First create Shop")
                        }
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

    @SuppressLint("WrongConstant")
    private fun intialize() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(2)?.isEnabled = false
        binding.bottomNavigationView.setupWithNavController(navController!!)
        binding.navView.setNavigationItemSelectedListener(this)
        val headerView: View = binding.navView.getHeaderView(0)
        img_profile = headerView.findViewById<CircleImageView>(R.id.img_profile)
        tv_name = headerView.findViewById<TextView>(R.id.tv_name)
        circle_progressview = headerView.findViewById<ImageView>(R.id.circle_progressview)

        userPreferences.getname.asLiveData().observe(this, androidx.lifecycle.Observer {
            tv_name?.setText(it)
        })
        userPreferences.getimage.asLiveData().observe(this, androidx.lifecycle.Observer {
            Glide.with(this).load(ApiService.IMAGE_BASE_URL + it)
                .diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.placeholder)
                .into(img_profile!!)
        })

        if (tabselect != null) {
            if (tabselect.equals("Shop")) {
                binding.tvTitle.text = "Shop"
                // binding.bottomNavigationView.selectedItemId=R.id.shop
                navController?.navigate(R.id.shopFragment)
            }
        }

        img_profile?.setOnClickListener {
            val intent = Intent(this, MyProfileActivity::class.java)
            startActivity(intent)
        }


        binding.bottomNavigationView.setOnItemReselectedListener { item ->

        }

        navController?.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.homeFragment) {
                binding.tvTitle.text = "Community"
                binding.imgMenu.visibility = View.VISIBLE
                binding.imgAdd.visibility = View.GONE
            } else if (destination.id == R.id.alertFragment) {
                binding.tvTitle.text = "Alerts"
                //  binding.imgMenu.visibility = View.GONE
                binding.imgAdd.visibility = View.VISIBLE
            } else if (destination.id == R.id.chatFragment) {
                binding.tvTitle.text = "Chats"
                // binding.imgMenu.visibility = View.GONE
                binding.imgAdd.visibility = View.GONE
            } else if (destination.id == R.id.shopFragment) {
                binding.tvTitle.text = "Shop"
                //    binding.imgMenu.visibility = View.GONE
                binding.imgAdd.visibility = View.GONE
            } else if (destination.id == R.id.notificationFragment) {
                binding.tvTitle.text = "Notifications"
                //   binding.imgMenu.visibility = View.GONE
                binding.imgAdd.visibility = View.GONE
            }
        }


        binding.imgMenu.setOnClickListener {
            binding.drawer.openDrawer(Gravity.START)
        }
    }

    private fun draweropen() {
        userPreferences = UserPreferences(this)
        var data = ""
        userPreferences.getprefrence.asLiveData()
            .observe(this, androidx.lifecycle.Observer { it ->
                data = it.toString()
                if (data.contains("petowner") && data.contains("petlover") && data.contains("volunteer")) {
                    circle_progressview.setBackgroundResource(R.drawable.three_color)
                } else if (data.contains("petowner") && data.contains("petlover")) {
                    circle_progressview.setBackgroundResource(R.drawable.two_color)
                } else if (data.contains("petlover") && data.contains("volunteer")) {
                    circle_progressview.setBackgroundResource(R.drawable.orangle_purple)
                } else if (data.contains("petowner") && data.contains("volunteer")) {
                    circle_progressview.setBackgroundResource(R.drawable.green_purple)
                } else if (data.contains("petowner") && !data.contains("petlover") && !data.contains(
                        "volunteer"
                    )
                ) {
                    circle_progressview.setBackgroundResource(R.drawable.one_color)
                } else if (!data.contains("petowner") && data.contains("petlover") && !data.contains(
                        "volunteer"
                    )
                ) {
                    circle_progressview.setBackgroundResource(R.drawable.orange_color)
                } else if (!data.contains("petowner") && !data.contains("petlover") && data.contains(
                        "volunteer"
                    )
                )
                    circle_progressview.setBackgroundResource(R.drawable.purple_color)
                else {
                    circle_progressview.setBackgroundResource(R.drawable.one_color)
                }
            })

        userPreferences.getname.asLiveData().observe(this, androidx.lifecycle.Observer { it ->
            tv_name?.setText(it)
        })
        userPreferences.getimage.asLiveData().observe(this, androidx.lifecycle.Observer {
            Glide.with(this).load(ApiService.IMAGE_BASE_URL + it)
                .diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.placeholder)
                .into(img_profile!!)
        })
    }


    fun AddAlert(view: View) {
        val intent = Intent(this, AddAlertActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("WrongConstant")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.setChecked(true)
        binding.drawer.closeDrawer(Gravity.START)
        val id: Int = item.getItemId()
        if (id == R.id.myprofile) {
            val intent = Intent(this, MyProfileActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.busprofile) {
            if (checkForInternet(this)) {
                viewmodel.checkshopdata()
            } else {
                toast("No internet connection")
            }
        } else if (id == R.id.settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.contact) {
            val intent = Intent(this, ContactUsActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.terms) {
            val intent = Intent(this, TermsandConditions::class.java)
            startActivity(intent)
        } else if (id == R.id.logout) {
            val dialog = MyDialog()
            dialog.Dialog_Logout(this, iterface)
            // MyDialog.Dialog_Logout(this)
        } else if (id == R.id.faq) {
            val intent = Intent(this, FaqActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.subscription) {
            val intent = Intent(this, CardPaymentActivity::class.java)
            startActivity(intent)
        }
        return false
    }


    private val iterface = object : LogoutInterface {
        override fun onLogout() {
            GlobalScope.launch(Dispatchers.Main) {
                userPreferences.saveimage("")
                userPreferences.savephone("")
                userPreferences.saveemail("")
                mySharedPreferences.setUserid("")
                mySharedPreferences.setToken("")
                mySharedPreferences.setUserName("")
                mySharedPreferences.setshowfirsttime(true)
                val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }


    }


}