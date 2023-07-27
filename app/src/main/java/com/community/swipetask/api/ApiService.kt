package com.community.swipetask.api

import com.community.swipetask.api.model.AddProduct
import com.community.swipetask.api.model.ProductListModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    /*Get Products Api*/
    @GET("get")
    fun getprodlist(): Call<List<ProductListModel>>?

    @Multipart
    @POST("add")
    fun addProduct(
        @Part("product_name") productName: RequestBody,
        @Part("product_type") productType: RequestBody,
        @Part("price") price: RequestBody,
        @Part("tax") tax: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<AddProduct>

    @FormUrlEncoded
    @POST("add")
    fun addProductWithoutFile(
        @Field("product_name") productName: String,
        @Field("product_type") productType: String,
        @Field("price") price: Double,
        @Field("tax") tax: Double,
        @Field("files") image: String
    ): Call<AddProduct>
}