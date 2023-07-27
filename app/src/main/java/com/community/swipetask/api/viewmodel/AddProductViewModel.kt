package com.community.swipetask.api.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.community.swipetask.api.ApiService
import com.community.swipetask.api.model.AddProduct
import com.community.swipetask.api.model.ProductListModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class AddProductViewModel : ViewModel() {

    private val apiBaseUrl = "https://app.getswipe.in/api/public/"
    private val _addProductResponse = MutableLiveData<AddProduct>()
    val addProductResponse: LiveData<AddProduct>
        get() = _addProductResponse
    private val _products = MutableLiveData<List<ProductListModel>>()
    val products: LiveData<List<ProductListModel>>
        get() = _products

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        val retrofit = Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val productService = retrofit.create(ApiService::class.java)
        val call = productService.getprodlist()

        call!!.enqueue(object : Callback<List<ProductListModel>> {
            override fun onResponse(
                call: Call<List<ProductListModel>>,
                response: Response<List<ProductListModel>>
            ) {
                if (response.isSuccessful) {
                    val productList = response.body()
                    _products.value = productList!!
                }
            }

            override fun onFailure(call: Call<List<ProductListModel>>, t: Throwable) {

            }
        })
    }

    fun addProduct(
        productName: String,
        productType: String,
        price: Double,
        tax: Double,
        imageFile: File
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val productService = retrofit.create(ApiService::class.java)

        val requestFile: RequestBody =
                imageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val imagePart: MultipartBody.Part =
                MultipartBody.Part.createFormData("files[]", imageFile.name, requestFile)

            val productNamePart: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), productName)
            val productTypePart: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), productType)
            val pricePart: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), price.toString())
            val taxPart: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), tax.toString())

            val call = productService.addProduct(
                productNamePart,
                productTypePart,
                pricePart,
                taxPart,
                imagePart
            )

            call.enqueue(object : Callback<AddProduct> {
                override fun onResponse(call: Call<AddProduct>, response: Response<AddProduct>) {
                    if (response.isSuccessful) {
                        val addProductResponse = response.body()
                        _addProductResponse.value = addProductResponse!!
                    }
                }

                override fun onFailure(call: Call<AddProduct>, t: Throwable) {
                }
            })

        }
    fun addProductWithoutFile(
        productName: String,
        productType: String,
        price: Double,
        tax: Double,
        imageFile: String
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val productService = retrofit.create(ApiService::class.java)

            val call = productService.addProductWithoutFile(
                productName,
                productType,
                price,
                tax,
                imageFile
            )

            call.enqueue(object : Callback<AddProduct> {
                override fun onResponse(call: Call<AddProduct>, response: Response<AddProduct>) {
                    if (response.isSuccessful) {
                        val addProductResponse = response.body()
                        _addProductResponse.value = addProductResponse!!
                    }
                }

                override fun onFailure(call: Call<AddProduct>, t: Throwable) {
                }
            })
    }
}
