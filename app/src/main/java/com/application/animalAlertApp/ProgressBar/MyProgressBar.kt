package com.application.animalAlertApp.ProgressBar

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import com.airbnb.lottie.LottieAnimationView
import com.application.animalAlertApp.R

class MyProgressBar {

    companion object {
        fun progress(context: Context?): Dialog {
            val dialog = Dialog(context!!)
            dialog.setContentView(R.layout.progress_bar)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.window!!.setBackgroundDrawable(
//            ColorDrawable(0)
//        )
            val animation_view =
                dialog.findViewById<View>(R.id.animation_view) as LottieAnimationView
            animation_view.playAnimation()
           // dialog.show()
            return dialog
        }
    }
}