package com.community.swipetask.api.model

import com.google.gson.annotations.SerializedName

data class ProductListModel(

    @SerializedName("image")
    val image: String? = null,

    @SerializedName("product_type")
    val productType: String? = null,

    @SerializedName("price")
    val price: Any? = null,

    @SerializedName("tax")
    val tax: Any? = null,

    @SerializedName("product_name")
    val productName: String? = null
)
