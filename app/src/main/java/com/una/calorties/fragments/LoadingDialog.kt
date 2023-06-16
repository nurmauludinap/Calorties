package com.una.calorties.fragments

import android.app.Activity
import android.app.AlertDialog
import android.view.WindowManager
import com.una.calorties.R


class LoadingDialog(val mActivity: Activity) {
    private lateinit var isdialog:AlertDialog
    fun startLoading(){
        /**set View*/
        val infalter = mActivity.layoutInflater
        val dialogView = infalter.inflate(R.layout.loading_dialog,null)
        /**set Dialog*/
        val builder = AlertDialog.Builder(mActivity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isdialog = builder.create()
        isdialog.show()
        isdialog.window?.setLayout(500, 500)
        isdialog.window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
    }
    fun isDismiss(){
        isdialog.dismiss()
    }
}
