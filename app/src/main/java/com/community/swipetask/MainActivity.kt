package com.community.swipetask

import ProductListFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.community.swipetask.utils.AppUtils

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppUtils.goNextFragmentAdd(this, ProductListFragment())

    }
}