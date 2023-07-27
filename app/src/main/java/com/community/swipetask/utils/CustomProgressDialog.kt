package com.community.swipetask.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.widget.ProgressBar
import com.community.swipetask.R

class CustomProgressDialog(private val context: Context) {
    private var dialog: Dialog? = null
    fun showDialog() {
        dialog = Dialog(context)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.default_progress_dialog)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val progressBar = dialog!!.findViewById<View>(R.id.progress) as ProgressBar
        if (!(context as Activity).isFinishing) {
            dialog!!.show()
        }
    }

    fun dismiss() {
        if (dialog != null) {
            if (!(context as Activity).isFinishing) {
                dialog!!.dismiss()
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        val r = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(), r.displayMetrics
        ).toInt()
    }

    fun show() {}
}