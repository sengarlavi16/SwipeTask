package com.community.swipetask.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.community.swipetask.R

object AppUtils {
    fun goNextFragmentReplace(context: Context, fragment: Fragment?) {
        val fragmentTransaction =
            (context as FragmentActivity).supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.FrameLayout, fragment!!)
        fragmentTransaction.commit()
    }

    fun goNextFragmentAdd(context: Context, fragment: Fragment?) {
        val fragmentTransaction =
            (context as FragmentActivity).supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.FrameLayout, fragment!!)
        fragmentTransaction.commit()
    }

    fun goFragmentReplaceWithBackStack(context: Context, fragment: Fragment?) {
        val fragmentTransaction =
            (context as FragmentActivity).supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.FrameLayout, fragment!!)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun goFragmentAddWithoutBackStack(context: Context, fragment: Fragment?) {
        val fragmentTransaction =
            (context as FragmentActivity).supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.FrameLayout, fragment!!)
        fragmentTransaction.addToBackStack(null)
        //fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss()
    }

    fun goFragmentReplaceWithoutBackStack(context: Context, fragment: Fragment?) {
        val fragmentTransaction =
            (context as FragmentActivity).supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.FrameLayout, fragment!!)
        fragmentTransaction.addToBackStack(null)
        //fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss()
    }

    fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isActive) {
            if (activity.currentFocus != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
            }
        }
    }
}