package com.community.swipetask.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import com.community.swipetask.R

class NetworkCheck
    (var context: Context) {
    companion object {
        var alert: AlertDialog? = null
        fun isConnected(context: Context): Boolean {
            var flag = false
            val connManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            val mMobileNewtork = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            if (mWifi!!.isConnected || mMobileNewtork!!.isConnected) {
                flag = true
            }
            return flag
        }

        fun showNetworkFailureAlert(context: Context?) {
            if (context != null && !(context as Activity).isFinishing && (alert == null || alert != null) && !alert!!.isShowing) {
                val builder = AlertDialog.Builder(context)
                builder.setMessage(context.getResources().getString(R.string.no_network_message))
                    .setTitle(context.getResources().getString(R.string.no_internet))
                    .setCancelable(false)
                    .setNegativeButton(
                        "OK"
                    ) { dialog, id -> }
                alert = builder.create()
                alert!!.show()
                val nbutton = alert!!.getButton(DialogInterface.BUTTON_NEGATIVE)
                nbutton.setTextColor(context.getResources().getColor(R.color.black))
            }
        }
    }
}