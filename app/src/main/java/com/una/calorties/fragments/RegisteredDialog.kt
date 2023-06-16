package com.una.calorties.fragments

import android.app.Activity
import android.app.AlertDialog
import android.view.WindowManager
import com.una.calorties.R


class RegisteredDialog(val mActivity: Activity) {
    private lateinit var isdialog:AlertDialog


    fun show(){
        /**set View*/
        val infalter = mActivity.layoutInflater
        val dialogView = infalter.inflate(R.layout.registered_dialog,null)
        /**set Dialog*/
        val builder = AlertDialog.Builder(mActivity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isdialog = builder.create()
        isdialog.show()
        isdialog.window?.setLayout(800, 650)
        isdialog.window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
    }
    fun isDismiss(){
        isdialog.dismiss()
    }
}
