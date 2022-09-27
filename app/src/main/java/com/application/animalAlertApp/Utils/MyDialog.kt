package com.application.animalAlertApp.Utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.application.animalAlertApp.Adapters.AlertReportAdapter
import com.application.animalAlertApp.Adapters.ShopDetailPagerAdapter
import com.application.animalAlertApp.Interfaces.*
import com.application.animalAlertApp.R
import com.application.animalAlertApp.data.Response.MessageXX
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.io.File

class MyDialog {

    fun Alert_popup(context: Context){
        try {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.alert_popup)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val mainview = dialog.findViewById<View>(R.id.mainview) as ConstraintLayout
            mainview.layoutParams.width = getHeightWidth("width", context) - 20

            dialog.show()

        } catch (e: Exception) {
            e.printStackTrace()
        }


        }

    fun Dialog_Add_Pet(context: Context?, addPetInteface: AddPetInteface) {
        try {
            val dialog = Dialog(context!!)
            dialog.setContentView(R.layout.pet_add_sucessfully)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val mainview = dialog.findViewById<View>(R.id.mainview) as ConstraintLayout
            mainview.layoutParams.width = getHeightWidth("width", context) - 20
            val bt_addmore = dialog.findViewById<Button>(R.id.bt_addmore)
            val bt_continue = dialog.findViewById<Button>(R.id.bt_continue)
            dialog.show()
            bt_addmore.setOnClickListener {
                dialog.dismiss()
                addPetInteface.OnAddMore()

            }
            bt_continue.setOnClickListener {
                addPetInteface.OnContinue()
                dialog.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun show_report_dialog(context: Context?, report: AlertReport, alertid: String) {
        try {
            val list = ArrayList<String>()
            val dialog = Dialog(context!!)
            dialog.setContentView(R.layout.report_items)
            dialog.setCanceledOnTouchOutside(true)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val mainview = dialog.findViewById<View>(R.id.mainview) as ConstraintLayout
            mainview.layoutParams.width = getHeightWidth("width", context)
            val bt_submit = dialog.findViewById<AppCompatButton>(R.id.bt_submit)
            val recyclerview = dialog.findViewById<RecyclerView>(R.id.recyclerview)
            val inter = object : SelectReportOption {
                override fun onOptionSelect(pos: Int, text: String) {
                    list.add(text)
                }
                override fun onOptionDeSelect(pos: Int, text: String) {
                    list.remove(text)
                }
            }
            val adapter = AlertReportAdapter(context, inter)
            recyclerview.adapter = adapter
            dialog.show()

            bt_submit.setOnClickListener {
                if (list.size > 0) {
                    report.onDoReport(alertid, list.toString())
                    dialog.dismiss()
                } else {
                    context.toast("Please Select Reason")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun see_pet_detail(
        context: Context?,
        data: MessageXX,
        status: String,
        updateInterface: PetsUpdateInterface
    ) {
        try {
            val dialog = Dialog(context!!)
            dialog.setContentView(R.layout.see_pet_detail)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val mainview = dialog.findViewById<View>(R.id.mainview) as ConstraintLayout
              mainview.layoutParams.width = getHeightWidth("width", context) - 50
            val viewPager = dialog.findViewById<ViewPager2>(R.id.viewPager)
            val tab_layout = dialog.findViewById<TabLayout>(R.id.tab_layout)

            val tv_name = dialog.findViewById<TextView>(R.id.tv_name)
            val tv_color = dialog.findViewById<TextView>(R.id.tv_color)
            val tv_breed = dialog.findViewById<TextView>(R.id.tv_breed)
            val tv_type = dialog.findViewById<TextView>(R.id.tv_type)

            val tv_description = dialog.findViewById<TextView>(R.id.tv_description)
            val img_close = dialog.findViewById<ImageView>(R.id.img_close)
            val img_delete = dialog.findViewById<ImageView>(R.id.img_delete)
            val img_edit = dialog.findViewById<ImageView>(R.id.img_edit)

            if (status.equals("MyPets")) {
                img_delete.visibility = View.VISIBLE
                img_edit.visibility = View.VISIBLE
            } else {
                img_delete.visibility = View.GONE
                img_edit.visibility = View.GONE
            }
            img_delete.setOnClickListener {
                updateInterface.ondeletepet()
                dialog.dismiss()
            }
            img_edit.setOnClickListener {
                updateInterface.oneditpet()
                dialog.dismiss()
            }

            val adapter = ShopDetailPagerAdapter(context)
            adapter.setItem(data.petImages)
            viewPager.adapter = adapter

            tv_name.setText(data.petName)
            tv_color.setText(data.petColor)
            tv_breed.setText(data.petBreed)
            tv_description.setText(data.description)
            tv_type.setText(data.petType)

            TabLayoutMediator(tab_layout, viewPager) { tab, position ->
            }.attach()

            dialog.show()

            img_close.setOnClickListener {
                dialog.dismiss()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun Dialog_Change_number(context: Context?, interafce: ChangePhoneInterface) {
        try {
            val dialog = Dialog(context!!)
            dialog.setContentView(R.layout.change_num_sucessfully)
            dialog.setCanceledOnTouchOutside(true)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val mainview = dialog.findViewById<View>(R.id.mainview) as ConstraintLayout
            mainview.layoutParams.width = getHeightWidth("width", context) - 20
            val bt_addmore = dialog.findViewById<Button>(R.id.bt_addmore)
            val et_mobile = dialog.findViewById<EditText>(R.id.et_mobile)
            dialog.show()
            bt_addmore.setOnClickListener {
                if (!et_mobile.text.toString().isEmpty()) {
                    interafce.onchange(et_mobile.text.toString())
                    dialog.dismiss()
                } else {
                    Toast.makeText(context, "Please Enter PhoneNo", Toast.LENGTH_SHORT).show()
                    //  dialog.dismiss()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun Dialog_Logout(context: Context?, iterface: LogoutInterface) {
        try {
            val dialog = Dialog(context!!)
            dialog.setContentView(R.layout.logout)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val mainview = dialog.findViewById<View>(R.id.mainview) as ConstraintLayout
            mainview.layoutParams.width = getHeightWidth("width", context) - 20
            val bt_cancel = dialog.findViewById<Button>(R.id.bt_cancel)
            val bt_logout = dialog.findViewById<Button>(R.id.bt_logout)
            dialog.show()
            bt_cancel.setOnClickListener {
                dialog.dismiss()
            }
            bt_logout.setOnClickListener {
                dialog.dismiss()
                iterface.onLogout()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun send_Image(
        context: Context?,
        file: File,
        uri: Uri,
        sendinterface: SendImageInterface
    ) {
        try {
            val dialog = Dialog(context!!)
            dialog.setContentView(R.layout.send_chat_image)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.BLACK))
            dialog.getWindow()?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            val image = dialog.findViewById<ImageView>(R.id.image)
            Glide.with(context).load(file).diskCacheStrategy(DiskCacheStrategy.ALL).into(image)
            //  image.setImageBitmap(bitmap)
            val send = dialog.findViewById<MaterialButton>(R.id.send)
            val close = dialog.findViewById<ImageView>(R.id.close)
            dialog.show()
            send.setOnClickListener {
                dialog.dismiss()
                sendinterface.onImagesend(file, uri)
            }
            close.setOnClickListener {
                dialog.dismiss()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun getHeightWidth(mode: String, context: Context): Int {
        val wm = context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y
        var lenthval = 0
        if (mode.equals("height", ignoreCase = true)) {
            lenthval = height
        } else if (mode.equals("width", ignoreCase = true)) {
            lenthval = width
        }
        return lenthval
    }

}